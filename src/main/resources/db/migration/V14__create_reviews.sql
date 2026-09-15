-- V14: Product reviews
CREATE TABLE IF NOT EXISTS reviews (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id),
    product_id UUID NOT NULL REFERENCES products(id),
    rating DECIMAL(2,1) NOT NULL,
    title VARCHAR(512),
    body TEXT,
    helpful_count INT NOT NULL DEFAULT 0,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    approved BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT FALSE, deleted_at TIMESTAMP WITH TIME ZONE,
    UNIQUE(user_id, product_id)
);
