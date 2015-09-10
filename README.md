# fold

#### LAN-centric IoT stack

* a REST machine with Jetty server, Apache client
* local data stores: Neo4j graph database, Eclipse filesystem API
* local chart and diagram generation, with gnuplot and graphviz
* Eclipse (headless, RCP) for modularity and extensibility
* Java 7 or 8 runtime
* runs on Raspberry Pi 2, ODroid(C1+, XU4), other ARM7 Linux, and x86 Linux

#### getting started

To get started using fold, see [fold-runtime](https://github.com/cjdaly/fold-runtime).

This Neo4j [guest blog post](http://neo4j.com/blog/neo4j-on-raspberry-pi/) describes some usage scenarios and talks about LAN-centric IoT.

#### fold reference Things

* Cali - Raspberry Pi 2
* Chase - ODroid-XU4
* [Marshall](https://github.com/cjdaly/fold/wiki/fold-Thing-Marshall) - Raspberry Pi 2
* [Rubble](https://github.com/cjdaly/fold/wiki/fold-Thing-Rubble) - ODroid-C1
* [Skye](https://github.com/cjdaly/fold/wiki/fold-Thing-Skye) - ODroid-U3
* Zuma - ODroid-C1+

#### going deeper

Channels are the primary unit of extensibility in fold.
See the [channel guide](https://github.com/cjdaly/fold/wiki/fold-channel-guide) for details.

See the [development notes](https://github.com/cjdaly/fold/wiki/fold-development),
if you're into that sort of thing.
