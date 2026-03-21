CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    avatar_url VARCHAR(500),
    fcm_token VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS contents (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    imdb_id VARCHAR(20) NOT NULL UNIQUE,
    title VARCHAR(300) NOT NULL,
    type VARCHAR(20),
    poster_url VARCHAR(500),
    year INTEGER,
    rating DECIMAL(3, 1),
    genre VARCHAR(200),
    synopsis TEXT,
    trailer_url VARCHAR(500),
    runtime_minutes INTEGER,
    cached_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS reminders (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    content_id INTEGER NOT NULL,
    scheduled_at TIMESTAMP NOT NULL,
    recurrence VARCHAR(10) DEFAULT 'ONCE',
    message VARCHAR(255),
    status VARCHAR(15) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (content_id) REFERENCES contents(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_reminders_scheduled ON reminders(scheduled_at, status);
CREATE INDEX IF NOT EXISTS idx_reminders_user_id ON reminders(user_id);
