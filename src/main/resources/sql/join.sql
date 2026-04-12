SELECT e.id, e.name, e.salary, e.department_id
FROM employee e
         JOIN (
    SELECT department_id, AVG(salary) AS avg_salary
    FROM employee
    GROUP BY department_id
) dept_avg ON e.department_id = dept_avg.department_id
WHERE e.salary > dept_avg.avg_salary;