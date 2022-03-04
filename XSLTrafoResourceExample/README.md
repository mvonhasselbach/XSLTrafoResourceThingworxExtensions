# XSLTrafoResourceExample Thingworx Extension #
### Example application of XSLTrafoResource ###

These examples are still quite basic right now!

## Setup ##
In __XSLTestThing__ there is a __Setup__ service that must be invoked once. This will copy a *cpq.zip* file to the __SystemRepository/cpq path__ and extract it there. This directory will then have a set of xml and xsl files that are used in the __XMLTrafo*__ services in __XSLTestThing__ . 

## Usage ##
The __XMLTrafo*__ services in __XSLTestThing__ showcase how xml and xsl data from different sources (input XML parameters or files) can be used. 

In addition there is a __XMLTrafoTestMashup__ that shows how the output of the service can be used in a Mashup. Be aware that linked resources like images should have an absolute URL, at least relative to */Thingworx*. In the example I use a MediaEntity that is referenced in __SystemRepository/cpq/config/config.generic.xsl__ .