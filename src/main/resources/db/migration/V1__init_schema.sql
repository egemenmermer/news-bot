-- Create bots table
CREATE TABLE bots (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    api_key VARCHAR(255) NOT NULL,
    api_secret VARCHAR(255) NOT NULL,
    instagram_username VARCHAR(255),
    instagram_password VARCHAR(255),
    instagram_access_token TEXT,
    pexels_api_key VARCHAR(255),
    mediastack_api_key VARCHAR(255),
    fetch_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    post_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP + INTERVAL '30 minutes',
    last_run TIMESTAMP
);

-- Create news table
CREATE TABLE news (
    id SERIAL PRIMARY KEY,
    bot_id BIGINT REFERENCES bots(id) ON DELETE CASCADE,
    title TEXT,
    content TEXT,
    description TEXT,
    image_url TEXT,
    generated_image_path TEXT,
    published_at TIMESTAMP NOT NULL,
    processed BOOLEAN NOT NULL DEFAULT FALSE,
    posted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create instagram_posts table
CREATE TABLE instagram_posts (
    id SERIAL PRIMARY KEY,
    bot_id BIGINT REFERENCES bots(id) ON DELETE CASCADE,
    news_id BIGINT REFERENCES news(id) ON DELETE CASCADE,
    image_url TEXT,
    caption TEXT,
    post_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    instagram_post_id TEXT,
    retry_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    posted_at TIMESTAMP,
    CONSTRAINT chk_post_status CHECK (post_status IN ('PENDING', 'IMAGE_GENERATED', 'POSTED', 'FAILED'))
);

-- Create bot_logs table
CREATE TABLE bot_logs (
    id SERIAL PRIMARY KEY,
    bot_id INT REFERENCES bots(id) ON DELETE CASCADE,
    log_type VARCHAR(50) NOT NULL,
    log_message TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_log_type CHECK (log_type IN ('INFO', 'WARNING', 'ERROR'))
);

-- Create fetch_logs table
CREATE TABLE fetch_logs (
    id SERIAL PRIMARY KEY,
    bot_id INT REFERENCES bots(id) ON DELETE CASCADE,
    fetched_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'SUCCESS',
    fetched_count INT NOT NULL DEFAULT 0,
    log_message TEXT,
    CONSTRAINT chk_fetch_status CHECK (status IN ('SUCCESS', 'FAILED'))
);

-- Create post_logs table
CREATE TABLE post_logs (
    id SERIAL PRIMARY KEY,
    bot_id INT REFERENCES bots(id) ON DELETE CASCADE,
    post_type VARCHAR(50) DEFAULT 'INSTAGRAM',
    platform VARCHAR(50) DEFAULT 'INSTAGRAM',
    post_id INT,
    status VARCHAR(50) NOT NULL DEFAULT 'SUCCESS',
    log_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_post_status CHECK (status IN ('SUCCESS', 'FAILED'))
);

-- Create indexes
CREATE INDEX idx_news_processed_posted ON news(processed, posted);
CREATE INDEX idx_instagram_posts_status ON instagram_posts(post_status);
CREATE INDEX idx_instagram_posts_bot_id ON instagram_posts(bot_id); 