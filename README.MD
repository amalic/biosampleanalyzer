Rewrite of the parser code of https://github.com/metadatacenter/biosample-analyzer

The original code used to import the original XML document into a in memory DOM tree. This lead to Out of Memory exceptions for large biosample files.

Run org.semanticscience.Application. You will need to provide three command line arguments:
1. input-file (xml)
2. output-file (csv)
3. an bioportal api-key
