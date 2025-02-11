-- Drop unnecessary tables
DROP TABLE IF EXISTS tweets CASCADE;

-- Modify existing bots table
ALTER TABLE bots 
ADD COLUMN instagram_username VARCHAR(255),
ADD COLUMN instagram_password VARCHAR(255),
ADD COLUMN instagram_access_token TEXT,
ADD COLUMN pexels_api_key VARCHAR(255),
ADD COLUMN mediastack_api_key VARCHAR(255);

-- Rename and update news_article table
ALTER TABLE news_article RENAME TO news;
ALTER TABLE news
ADD COLUMN image_url TEXT,
ADD COLUMN generated_image_path TEXT,
ADD COLUMN posted BOOLEAN DEFAULT FALSE,
ALTER COLUMN processed SET DEFAULT FALSE;

-- Create instagram_posts table
CREATE TABLE IF NOT EXISTS instagram_posts (
    id SERIAL PRIMARY KEY,
    bot_id INT REFERENCES bots(id) ON DELETE CASCADE,
    news_id INT REFERENCES news(id) ON DELETE CASCADE,
    image_url TEXT NOT NULL,
    caption TEXT,
    post_status VARCHAR(50) NOT NULL CHECK (post_status IN ('PENDING', 'IMAGE_GENERATED', 'POSTED', 'FAILED')),
    instagram_post_id TEXT,
    retry_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    posted_at TIMESTAMP
);

-- Update post_logs table to include Instagram specific fields
ALTER TABLE post_logs
ADD COLUMN post_type VARCHAR(50) DEFAULT 'INSTAGRAM',
ADD COLUMN post_id INT,
ADD COLUMN platform VARCHAR(50) DEFAULT 'INSTAGRAM';

-- Add new configuration types for Instagram
INSERT INTO bot_configurations (bot_id, config_type, config_value)
SELECT 
    id as bot_id,
    'INSTAGRAM_HASHTAGS' as config_type,
    '#news #update #breaking' as config_value
FROM bots
WHERE NOT EXISTS (
    SELECT 1 FROM bot_configurations 
    WHERE config_type = 'INSTAGRAM_HASHTAGS'
);

-- Add indexes for better performance
CREATE INDEX IF NOT EXISTS idx_instagram_posts_status ON instagram_posts(post_status);
CREATE INDEX IF NOT EXISTS idx_instagram_posts_bot_id ON instagram_posts(bot_id);
CREATE INDEX IF NOT EXISTS idx_news_processed_posted ON news(processed, posted);