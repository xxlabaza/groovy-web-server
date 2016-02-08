#!/usr/bin/env groovy

/*
 * Copyright 2016 xxlabaza.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import com.sun.net.httpserver.*

final DEFAULT_PORT = 8080
final DEFAULT_SOURCE = './'
final AVAILABE_TYPES = [
    "css":  "text/css",
    "gif":  "image/gif",
    "html": "text/html",
    "jpg":  "image/jpeg",
    "js":   "application/javascript",
    "png":  "image/png",
    "svg":  "image/svg+xml",
]



def cli = new CliBuilder(usage: 'server.groovy [options]',
                         header:'\nOptions:')
cli.with {
    h longOpt: 'help', 'Show usage information'
    p longOpt: 'port', args: 1, argName: 'port', "Set port to use. Default is $DEFAULT_PORT"
    s longOpt: 'source', args: 1, argName: 'folder', "Declare source folder for static. Default is $DEFAULT_SOURCE"
}
def options = cli.parse(args)
if (options.h) {
    cli.usage()
    System.exit(0)
}



def port = options.port.toInteger() ?: DEFAULT_PORT
def root = new File(options.source ?: DEFAULT_SOURCE)
def server = HttpServer.create(new InetSocketAddress(port), 0)

server.createContext('/', { exchange ->
    if (!'GET'.equalsIgnoreCase(exchange.requestMethod)) {
        exchange.sendResponseHeaders(405, 0)
        exchange.responseBody.close()
        return
    }

    def path = exchange.requestURI.path
    println "[${new Date().format('HH:mm:ss.Ms')}]: GET $path"
    // path starts with /
    def file = new File(root, path.substring(1))
    if (file.isDirectory()) {
        file = new File(file, 'index.html')
    }
    if (file.exists()) {
        exchange.responseHeaders.set('Content-Type', AVAILABE_TYPES[file.name.split(/\./)[-1]] ?: 'text/plain')
        exchange.sendResponseHeaders(200, 0)
        file.withInputStream {
            exchange.responseBody << it
        }
        exchange.responseBody.close()
    } else {
        exchange.sendResponseHeaders(404, 0)
        exchange.responseBody.close()
    }
} as HttpHandler)

server.start()
println "Started HTTP server on port ${port}, from ${root}"
