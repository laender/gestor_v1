echo off
"C:\Program Files\PostgreSQL\9.4\bin\pg_dump.exe" -i  -h localhost -p 5432 -U postgres -F c -v  -f "C:\Users\laender\Google Drive\backup_banco\05-11-PM-23-21-26.backup" gestor
exit
