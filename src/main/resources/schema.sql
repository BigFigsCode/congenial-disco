-- schema.sql  (DEV: drop & recreate; safe to re-run)
PRAGMA foreign_keys = ON;

-- Drop existing tables in the right order
DROP TABLE IF EXISTS workout_exercise;
DROP TABLE IF EXISTS session;
DROP TABLE IF EXISTS workout;
DROP TABLE IF EXISTS exercise;
DROP TABLE IF EXISTS profile_equipment;
DROP TABLE IF EXISTS equipment;
DROP TABLE IF EXISTS profile;

-- ===== Core Tables =====

-- Profile Table
CREATE TABLE IF NOT EXISTS profile (
                                       id            INTEGER PRIMARY KEY AUTOINCREMENT,
                                       name          TEXT    NOT NULL,
                                       level         TEXT    NOT NULL,
                                       goal          TEXT    NOT NULL,
                                       days_per_week INTEGER NOT NULL CHECK(days_per_week BETWEEN 1 AND 7),
    created_at    TEXT    NOT NULL DEFAULT (datetime('now')),
    updated_at    TEXT    NOT NULL DEFAULT (datetime('now'))
    );

-- Equipment Table (2025-11-03)
-- Dedicated table to store gym equipment items
CREATE TABLE IF NOT EXISTS equipment (
                                         id    INTEGER PRIMARY KEY AUTOINCREMENT,
                                         name  TEXT NOT NULL UNIQUE,
                                         type  TEXT,        -- e.g., Dumbbell, Machine
                                         notes TEXT         -- descriptions or safety info
);

-- Profile_Equipment Table (links profiles to equipment)
CREATE TABLE IF NOT EXISTS profile_equipment (
                                                 profile_id   INTEGER NOT NULL,
                                                 equipment_id INTEGER NOT NULL,
                                                 PRIMARY KEY (profile_id, equipment_id),
    FOREIGN KEY (profile_id)   REFERENCES profile(id)   ON DELETE CASCADE,
    FOREIGN KEY (equipment_id) REFERENCES equipment(id) ON DELETE RESTRICT
    );

-- Exercise Table
CREATE TABLE IF NOT EXISTS exercise (
                                        id         INTEGER PRIMARY KEY AUTOINCREMENT,
                                        profile_id INTEGER NOT NULL,
                                        name       TEXT NOT NULL,
                                        muscle     TEXT NOT NULL,
                                        equipment  TEXT,
                                        difficulty TEXT,
                                        type       TEXT NOT NULL,
                                        FOREIGN KEY (profile_id) REFERENCES profile(id) ON DELETE CASCADE,
    UNIQUE (profile_id, name)
    );

-- Workout Table
CREATE TABLE IF NOT EXISTS workout (
                                       id                         INTEGER PRIMARY KEY AUTOINCREMENT,
                                       profile_id                 INTEGER NOT NULL,
                                       name                       TEXT NOT NULL,
                                       day_of_week                INTEGER CHECK(day_of_week BETWEEN 1 AND 7),
    rest_between_sets_sec      INTEGER NOT NULL DEFAULT 90  CHECK (rest_between_sets_sec >= 0),
    rest_between_exercises_sec INTEGER NOT NULL DEFAULT 120 CHECK (rest_between_exercises_sec >= 0),
    active                     INTEGER NOT NULL DEFAULT 1,
    created_at                 TEXT NOT NULL DEFAULT (datetime('now')),
    updated_at                 TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY (profile_id) REFERENCES profile(id) ON DELETE CASCADE
    );

-- Workout_Exercise Table
CREATE TABLE IF NOT EXISTS workout_exercise (
                                                workout_id          INTEGER NOT NULL,
                                                exercise_id         INTEGER NOT NULL,
                                                order_idx           INTEGER NOT NULL CHECK(order_idx >= 1),
    target_sets         INTEGER NOT NULL CHECK(target_sets > 0),
    target_reps_or_secs INTEGER NOT NULL CHECK(target_reps_or_secs > 0),
    PRIMARY KEY (workout_id, order_idx),
    UNIQUE (workout_id, exercise_id, order_idx),
    FOREIGN KEY (workout_id)  REFERENCES workout(id)  ON DELETE CASCADE,
    FOREIGN KEY (exercise_id) REFERENCES exercise(id) ON DELETE RESTRICT
    );

-- Session Table
CREATE TABLE IF NOT EXISTS session (
                                       id            INTEGER PRIMARY KEY AUTOINCREMENT,
                                       profile_id    INTEGER NOT NULL,
                                       workout_id    INTEGER,
                                       date_iso      TEXT NOT NULL,
                                       total_minutes INTEGER NOT NULL CHECK(total_minutes >= 0),
    rpe           INTEGER CHECK(rpe BETWEEN 1 AND 10),
    notes         TEXT,
    FOREIGN KEY (profile_id) REFERENCES profile(id) ON DELETE CASCADE,
    FOREIGN KEY (workout_id) REFERENCES workout(id) ON DELETE SET NULL
    );

-- Indexes
CREATE INDEX IF NOT EXISTS idx_profile_equipment_profile ON profile_equipment(profile_id);
CREATE INDEX IF NOT EXISTS idx_profile_equipment_equipment ON profile_equipment(equipment_id);
CREATE INDEX IF NOT EXISTS idx_exercise_profile ON exercise(profile_id);
CREATE INDEX IF NOT EXISTS idx_exercise_profile_name ON exercise(profile_id, name);
CREATE INDEX IF NOT EXISTS idx_workout_profile ON workout(profile_id);
CREATE INDEX IF NOT EXISTS idx_workout_day ON workout(profile_id, day_of_week);
CREATE INDEX IF NOT EXISTS idx_workout_exercise_w ON workout_exercise(workout_id);
CREATE INDEX IF NOT EXISTS idx_workout_exercise_e ON workout_exercise(exercise_id);
CREATE INDEX IF NOT EXISTS idx_session_profile_date ON session(profile_id, date_iso);
