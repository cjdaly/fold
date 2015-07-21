# fold

#### LAN-centric IoT stack

* Jetty server + Apache client = REST machine
* local data stores: Neo4j graph database, Eclipse filesystem API
* local chart generation, with gnuplot
* Eclipse (headless, RCP) for modularity and extensibility
* Java 7 or 8 runtime
* runs on Raspberry Pi2, ODroid(C1, U3), other ARM7 Linux, and x86 Linux

#### getting started

To get started using fold, see [fold-runtime](https://github.com/cjdaly/fold-runtime).

#### fold reference Things

* [Marshall](https://github.com/cjdaly/fold/wiki/fold-Thing-Marshall) - Raspberry Pi 2
* [Rubble](https://github.com/cjdaly/fold/wiki/fold-Thing-Rubble) - ODroid-C1
* [Skye](https://github.com/cjdaly/fold/wiki/fold-Thing-Skye) - ODroid-U3

#### going deeper

Channels are the primary unit of extensibility in fold.
See the [channel guide](https://github.com/cjdaly/fold/wiki/fold-channel-guide) for details.

See the [developement notes](https://github.com/cjdaly/fold/wiki/fold-development),
if you're into that sort of thing.
