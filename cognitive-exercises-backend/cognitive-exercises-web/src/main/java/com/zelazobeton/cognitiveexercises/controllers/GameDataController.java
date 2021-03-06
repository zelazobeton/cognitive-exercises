package com.zelazobeton.cognitiveexercises.controllers;

import static com.zelazobeton.cognitiveexercieses.constant.FileConstants.FORWARD_SLASH;
import static com.zelazobeton.cognitiveexercieses.constant.FileConstants.GAMES_DATA_FOLDER;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zelazobeton.cognitiveexercieses.model.GameDataDto;
import com.zelazobeton.cognitiveexercieses.service.GameDataService;
import com.zelazobeton.cognitiveexercieses.service.MessageService;
import com.zelazobeton.cognitiveexercises.ExceptionHandling;

@RestController
@RequestMapping(path = "/games")
public class GameDataController extends ExceptionHandling {
    private final GameDataService gamesDataService;

    public GameDataController(MessageService messageService, GameDataService gamesDataService) {
        super(messageService);
        this.gamesDataService = gamesDataService;
    }

    @GetMapping(path = "/data", produces = { "application/json" })
    public ResponseEntity<List<GameDataDto>> getGames() {
        return new ResponseEntity<>(gamesDataService.getGamesData(), HttpStatus.OK);
    }

    @GetMapping(path = "/icon/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getMemoryTileImage(@PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(GAMES_DATA_FOLDER + FORWARD_SLASH + fileName));
    }
}
