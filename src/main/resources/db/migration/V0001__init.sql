create table reminders
(
    id uuid not null constraint reminders_pk primary key,
    employee_id uuid not null,
    text text not null,
    timestamp timestamp with time zone not null,
    is_recurring boolean not null,
    recurrence_interval int,
    recurrence_frequency int
);

create table occurrences
(
    id uuid not null constraint occurrences_pk primary key,
    reminder_id uuid not null,
    timestamp timestamp with time zone not null,
    is_acknowledged boolean not null,
    notification_sent bool default false
);



