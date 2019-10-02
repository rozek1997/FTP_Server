package com.goldenore.fileserver.pojo;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileAddress {

    private String physicalAdress;

    private String URIAdress;
}
