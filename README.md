# Описание

HTTP сервер на **Groovy** для работы со статикой

Запуск:

```bash
$> ./server.groovy -h
usage: server.groovy [options]

Options:
 -h,--help              Show usage information
 -p,--port <port>       Set port to use. Default is 8080
 -s,--source <folder>   Declare source folder for static. Default is ./

$> ./server.groovy -p 8080 -s ./
Started HTTP server on port 8080, from .
...
```
