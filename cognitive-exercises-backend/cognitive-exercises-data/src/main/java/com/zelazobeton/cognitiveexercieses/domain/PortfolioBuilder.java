package com.zelazobeton.cognitiveexercieses.domain;

import static com.zelazobeton.cognitiveexercieses.constant.FileConstants.AVATAR;
import static com.zelazobeton.cognitiveexercieses.constant.FileConstants.DEFAULT_AVATAR_FILENAME;
import static com.zelazobeton.cognitiveexercieses.constant.FileConstants.DEFAULT_AVATAR_FILE;
import static com.zelazobeton.cognitiveexercieses.constant.FileConstants.DIRECTORY_CREATED;
import static com.zelazobeton.cognitiveexercieses.constant.FileConstants.FORWARD_SLASH;
import static com.zelazobeton.cognitiveexercieses.constant.FileConstants.LOCALHOST_ADDRESS;
import static com.zelazobeton.cognitiveexercieses.constant.FileConstants.USER_FOLDER;
import static com.zelazobeton.cognitiveexercieses.constant.FileConstants.USER_IMAGE_PATH;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.zelazobeton.cognitiveexercieses.domain.security.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PortfolioBuilder {
    private PortfolioBuilder(){}

    public static Portfolio createPortfolioWithGeneratedAvatar(User user) throws IOException {
        return new Portfolio(user, generateAvatarAddress(user.getUsername()), 0L);
    }

    public static Portfolio createBootstrapPortfolioWithGeneratedAvatar(User user) throws IOException {
        return new Portfolio(user, generateAvatarAddressForBootstrapUsers(user.getUsername()), 0L);
    }

    private static String generateAvatarAddressForBootstrapUsers(String username) throws IOException {
        generateAvatar(username);
        return LOCALHOST_ADDRESS + USER_IMAGE_PATH + username + FORWARD_SLASH + DEFAULT_AVATAR_FILENAME;
    }

    private static String generateAvatarAddress(String username) throws IOException {
        generateAvatar(username);
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(USER_IMAGE_PATH + username + FORWARD_SLASH + DEFAULT_AVATAR_FILENAME)
                .toUriString();
    }

    private static void generateAvatar(String username) throws IOException {
        Path target = Paths.get(USER_FOLDER + FORWARD_SLASH + username + FORWARD_SLASH + AVATAR).toAbsolutePath().normalize();
        try {
            createFolderIfThereIsNone(target);
            URL website = new URL(generateRoboHashAddress());
            InputStream in = website.openStream();
            Files.copy(in, target.resolve(DEFAULT_AVATAR_FILENAME), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            log.info(ex.toString());
            Path defaultAvatarSrc = Paths.get(DEFAULT_AVATAR_FILE).toAbsolutePath().normalize();
            Files.copy(defaultAvatarSrc, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static String generateRoboHashAddress() {
        return "https://robohash.org/" + RandomStringUtils.randomAlphanumeric(10);
    }

    private static void createFolderIfThereIsNone(Path target) throws IOException {
        if(!Files.exists(target)) {
            Files.createDirectories(target);
            log.debug(DIRECTORY_CREATED + target);
        }
    }
}
