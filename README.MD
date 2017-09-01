Rewrite of the parser code of https://github.com/metadatacenter/biosample-analyzer

The original code used to import the original XML document into a in memory DOM tree. This lead to Out of Memory exceptions for large biosample files.
