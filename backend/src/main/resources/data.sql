INSERT INTO sitter_services (name) VALUES 
('Pet Boarding'), 
('Sitting'), 
('Pet Walking'), 
('Pet Taxi'), 
('Grooming') 
ON CONFLICT (name) DO NOTHING;