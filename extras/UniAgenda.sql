CREATE TABLE "account" (
  "account_id" uuid PRIMARY KEY,
  "username" varchar(50) UNIQUE,
  "email" varchar(50) UNIQUE,
  "password" varchar(50),
  "created_at" TIMESTAMP,
  "updated_at" TIMESTAMP
);


CREATE TABLE "task" (
  "task_id" serial PRIMARY KEY,
  "account_id" uuid,
  "task_title" varchar(50),
  "course_name" varchar(50),
  "task_description" text,
  "task_deadline" timestamp,
  "task_status" "ENUM(Not Started,In Progress,Completed)",
  "task_type" ENUM(Exam,Homework,Quiz,Project,Other)
  FOREIGN KEY (account_id) REFERENCES account(account_id) ON DELETE CASCADE
);

CREATE TABLE "organization" (
  "org_id" serial PRIMARY KEY,
  "account_id" uuid,
  "org_name" varchar(100),
  "org_role" varchar(50),
  "org_description" text,
  FOREIGN KEY (account_id) REFERENCES account(account_id) ON DELETE CASCADE
);

CREATE TABLE "event" (
  "event_id" serial PRIMARY KEY,
  "account_id" uuid,
  "event_title" varchar(50),
  "event_date" timestamp,
  "event_description" text,
  "location" varchar(50)
  FOREIGN KEY (account_id) REFERENCES account(account_id) ON DELETE CASCADE
);

