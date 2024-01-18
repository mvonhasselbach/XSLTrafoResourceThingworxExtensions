# XSLTrafoResource Thingworx Extension #
### Thingworx Extension that adds a Resource with a Javascript service to transform XML via XSL ###

This extension adds one service in the list of scripting snippets with which you can apply xsl transformations
to input xml. There are multiple options where the source xml and xsl can come from and where you can write it to.

The XML and XSL source can either come from input XML parameters or from files in a specified file repository. 
The result of teh transformation can either be returned as a String or can be written to a target file in the same file repository.
If you have very large input files to process, this may be a more performant option - XML parsing in Thingworx tend to be very slow 
and I hope that my implementation based on StreamSource is a bit better here.

When the xsl trafo comes from a File in the FileRepo, it will resolve references (e.g. in <includes/> and <imports/>) relative to the 
specified (top-level) xsl file. 

When using XML parameters (i.e. Thingworx variables of type XML) Thingworx doesn't allow DTD and xml processing instructions. 
You may have to strip them out in this case. An example of such a pre-processing is included in 
the __XSLTrafoResource Examples__ Extension in the __XSLTestThing.XMLTrafoFromFileRepoFiles()__ service. When getting 
the xml from a file in the FileReository, the parser is more permissive and allows DTD and ProcessingInstructions. 
See the __XSLTestThing.XMLTrafoFromFileRepoFiles2()__ service for an example.

 The Thingworx importable packages of this two extension is available from the [Releases section](../../../releases).
 
 For convenience I also added a command-line version __XSLTrafoCmd__ that uses the same (java default) XSL processor. 
 A runnable jar of it is added to the  [Releases section](../../../releases) as well.