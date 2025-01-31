-- 1️⃣ Önce bağımsız tabloyu oluştur
CREATE TABLE IF NOT EXISTS bots (
                                    id SERIAL PRIMARY KEY,
                                    name VARCHAR(255) NOT NULL UNIQUE,
                                    api_key VARCHAR(255) NOT NULL,
                                    api_secret VARCHAR(255) NOT NULL,
                                    fetch_time TIME NOT NULL DEFAULT '00:00:00',
                                    post_time TIME NOT NULL DEFAULT '00:30:00',
                                    last_run TIMESTAMP DEFAULT NULL
);

-- 2️⃣ Sonradan referans verilen `news_article` tablosunu oluştur
CREATE TABLE IF NOT EXISTS news_article (
                                            id SERIAL PRIMARY KEY,
                                            bot_id INT REFERENCES bots(id) ON DELETE CASCADE,
                                            title TEXT NOT NULL,
                                            content TEXT,
                                            published_at TIMESTAMP NOT NULL,
                                            processed BOOLEAN
);

-- 3️⃣ `tweets` tablosu artık `news_article`'a referans verebilir
CREATE TABLE IF NOT EXISTS tweets (
                                      id SERIAL PRIMARY KEY,
                                      bot_id INT REFERENCES bots(id) ON DELETE CASCADE,
                                      news_id INT REFERENCES news_article(id) ON DELETE CASCADE,
                                      content TEXT NOT NULL,
                                      status VARCHAR(50) NOT NULL,
                                      retry_count INT DEFAULT 0,
                                      scheduled_at TIMESTAMP DEFAULT NULL,
                                      created_at TIMESTAMP DEFAULT NOW()
);

-- 4️⃣ Diğer bağımlı tablolar
CREATE TABLE IF NOT EXISTS bot_logs (
                                        id SERIAL PRIMARY KEY,
                                        bot_id INT REFERENCES bots(id) ON DELETE CASCADE,
                                        log_type VARCHAR(50) NOT NULL,
                                        log_message TEXT NOT NULL,
                                        created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS bot_configurations (
                                                  id SERIAL PRIMARY KEY,
                                                  bot_id INT REFERENCES bots(id) ON DELETE CASCADE,
                                                  config_type VARCHAR(255) NOT NULL,
                                                  config_value TEXT NOT NULL,
                                                  created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS fetch_logs (
                                          id SERIAL PRIMARY KEY,
                                          bot_id INT REFERENCES bots(id) ON DELETE CASCADE,
                                          fetched_at TIMESTAMP DEFAULT NOW(),
                                          status VARCHAR(50) NOT NULL CHECK (status IN ('success', 'failed')),
                                          fetched_count INT NOT NULL,
                                          log_message TEXT DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS post_logs (
                                         id SERIAL PRIMARY KEY,
                                         bot_id INT REFERENCES bots(id) ON DELETE CASCADE,
                                         scheduled_at TIMESTAMP DEFAULT NULL,
                                         posted_at TIMESTAMP DEFAULT NOW(),
                                         status VARCHAR(50) NOT NULL CHECK (status IN ('success', 'failed')),
                                         log_message TEXT DEFAULT NULL
);