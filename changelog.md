# Changelog

##  7.2.0-3-SNAPSHOT
1. Updated JsonTypeInfo property of Filter to 'filterType' instead of 'operator' which doesn't exists
2. Pass additional field XContentType.JSON when updateField is being called -  https://github.com/koushikr/jelastic/pull/2
3. Added JelasticException which extends from Runtime exception and all exception extends JelasticException
4. Added changelog.md file