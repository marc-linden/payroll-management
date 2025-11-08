INSERT INTO Employer
VALUES (10001, 'Example One Inc.', 'af93a0a4-d207-4357-af02-90e419b25185', 'info@example-one.com');
INSERT INTO Employer
VALUES (10002, 'Example Two Inc.', '5ccb4755-5460-4f95-9e08-03d40f3e47f3', 'info@example-two.com');

INSERT INTO Employee
VALUES (4001, 'Franz', 'Meyer', 'franz.meyer@example-one.com', 'EXECUTIVE_BOARD', 'INTERNAL', 'MONTHLY_SALARY', 10001);
INSERT INTO Employee
VALUES (4002, 'Heinz', 'Mayer', 'heinz.mayer@example-one.com', 'EMPLOYEE', 'INTERNAL', 'MONTHLY_SALARY', 10001);
INSERT INTO Employee
VALUES (4003, 'Eugen', 'Schäfer', 'eugen.schaefer@example-one.com', 'EMPLOYEE', 'INTERNAL', 'HOURLY_WAGE', 10001);
INSERT INTO Employee
VALUES (4004, 'Lisa', 'Müller', 'lisa.mueller@example-one.com', 'HUMAN_RESOURCES', 'INTERNAL', 'MONTHLY_SALARY', 10001);

INSERT INTO Employee
VALUES (5001, 'Heinz', 'Mayer', 'heinz.mayer@example-two.com', 'EMPLOYEE', 'INTERNAL', 'MONTHLY_SALARY', 10002);
INSERT INTO Employee
VALUES (5002, 'Herbert', 'Förster', 'herbert.foerster@example-two.com', 'EMPLOYEE', 'EXTERNAL', 'MONTHLY_SALARY', 10002);
