CREATE TABLE IF NOT EXISTS bots (
                                    id SERIAL PRIMARY KEY,
                                    name VARCHAR(255) NOT NULL UNIQUE,  --SportsBot veya PoliticsBot
                                    api_key VARCHAR(255) NOT NULL,  -- Twitter API anahtarı (API Key)-burda abi 1 den fazla twitter hesabı olcagından hepsinin ayrı keyi
                                    api_secret VARCHAR(255) NOT NULL,  -- Twitter API gizli anahtarı (API Secret)-""" hepsinin ayrı secret ı
                                    fetch_time TIME NOT NULL DEFAULT '00:00:00',  -- Fetch işlem saati
                                    post_time TIME NOT NULL DEFAULT '00:30:00',  -- Post işlem saati-> şimdi burda fetch işlemi ne kadar sürer bilmiyom sunucuda kısa sürer ama ben bu pc de çalıştırırsam gibi düşün en az 20 dk da yapar benim 11 senelik macbook ondan böyle. ama 5 dk aralıkta yaparız.
                                    last_run TIMESTAMP DEFAULT NULL  -- Botun en son çalıştığı zaman (burda timestamp olarak çünkü salisesi bile lazım ne zaman çalıştıgının)
);

CREATE TABLE IF NOT EXISTS tweets (
                                      id SERIAL PRIMARY KEY,
                                      bot_id INT REFERENCES bots(id) ON DELETE CASCADE,
                                      news_id INT REFERENCES news_article(id) ON DELETE CASCADE ,
                                      content TEXT NOT NULL,
                                      status VARCHAR(50) NOT NULL ,
                                      retry_count INT DEFAULT 0,  -- Kaç kez tekrar denendiği
                                      scheduled_at TIMESTAMP DEFAULT NULL,
                                      created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS bot_logs (
                                    id SERIAL PRIMARY KEY,
                                    bot_id INT REFERENCES bots(id) ON DELETE CASCADE,
                                    log_type VARCHAR(50) NOT NULL,  -- INFO, ERROR
                                    log_message TEXT NOT NULL,  -- başarılı, hata mesajı
                                    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS bot_configurations (
                                              id SERIAL PRIMARY KEY,
                                              bot_id INT REFERENCES bots(id) ON DELETE CASCADE,
                                              config_type VARCHAR(255) NOT NULL,
                                              config_value TEXT NOT NULL,  -- hourly, 5
                                              created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS fetch_logs (
                                          id SERIAL PRIMARY KEY,
                                          bot_id INT REFERENCES bots(id) ON DELETE CASCADE,
                                          fetched_at TIMESTAMP DEFAULT NOW(),  -- Fetch zamanı
                                          status VARCHAR(50) NOT NULL CHECK (status IN ('success', 'failed')),  -- Fetch durum
                                          fetched_count INT NOT NULL,  -- Fetch edilen haber sayısı
                                          log_message TEXT DEFAULT NULL  -- Fetch hataları
);

CREATE TABLE IF NOT EXISTS post_logs (
                                         id SERIAL PRIMARY KEY,
                                         bot_id INT REFERENCES bots(id) ON DELETE CASCADE,
                                         scheduled_at TIMESTAMP DEFAULT NULL,  -- Post planlanan zamanı
                                         posted_at TIMESTAMP DEFAULT NOW(),  -- Tweet in postlandığı zaman
                                         status VARCHAR(50) NOT NULL CHECK (status IN ('success', 'failed')),  -- Post durum
                                         log_message TEXT DEFAULT NULL  -- Post işlemi hataları
);

CREATE TABLE news_article (
                              id SERIAL PRIMARY KEY,
                              bot_id INT REFERENCES bots(id) ON DELETE CASCADE,
                              title TEXT NOT NULL,
                              content TEXT,
                              published_at TIMESTAMP NOT NULL,
                              processed BOOLEAN
);
