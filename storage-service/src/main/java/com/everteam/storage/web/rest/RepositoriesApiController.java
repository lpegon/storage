package com.everteam.storage.web.rest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

import com.everteam.storage.converters.FileIdConverter;
import com.everteam.storage.domain.ESRepository;
import com.everteam.storage.drive.IDrive;
import com.everteam.storage.oauth2.OAuth2Utils;
import com.everteam.storage.service.RepositoryService;
import com.everteam.storage.utils.ESFileId;
import com.google.api.client.auth.oauth2.Credential;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-03-09T15:45:07.545Z")

@Controller
public class RepositoriesApiController implements RepositoriesApi {
    @Autowired
    RepositoryService repositoryService;

    @Autowired
    OAuth2Utils oauth2;

    @Override
    public ResponseEntity<List<ESRepository>> listRepositories() {
        try {
            return new ResponseEntity<List<ESRepository>>(
                    repositoryService.getRepositoryList(), HttpStatus.OK);
        } catch (Exception e) {
            throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<String> getRepositoryToken(
            @PathVariable("id") String encryptedId) {
        ESFileId id = new FileIdConverter().convert(encryptedId);
        IDrive drive = repositoryService.getDrive(id.getRepositoryId());
        Credential credential = null;
        try {
            oauth2.init(drive);
            credential = oauth2.loadCredential();
            if (credential == null || credential.getAccessToken() == null) {
                return new ResponseEntity<>(oauth2.newAuthorization(),
                        HttpStatus.SEE_OTHER);
            }
            return new ResponseEntity<String>(credential.getAccessToken(),
                    HttpStatus.OK);
        } catch (IOException | GeneralSecurityException
                | URISyntaxException e) {
            throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
        }
    }

}
