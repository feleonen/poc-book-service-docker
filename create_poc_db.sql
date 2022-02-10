USE MASTER
GO

CREATE DATABASE POC 
GO

CREATE LOGIN book_poc WITH PASSWORD = 'X!&2kf@M5v5j8VV$'
GO

USE POC
GO

CREATE USER book_poc FOR LOGIN book_poc
GO

EXEC sp_addrolemember 'db_owner', book_poc
GO
