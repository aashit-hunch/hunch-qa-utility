package org.hunch.utils;

import org.hunch.enums.core.RequestBodySchemaFileEnums;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GraphQLFileUtil {

    /**
     * Reads a .graphql file from an absolute or relative file system path.
     */
    public static String readGraphQLFromFileSystem(RequestBodySchemaFileEnums resourcePath) {
        String path = System.getProperty("user.dir")+"/src/main/resources/graphqlSchema/";
        try {
            return Files.readString(Paths.get(path+resourcePath.getValue()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read GraphQL file: " + path+resourcePath.getValue(), e);
        }
    }
}
