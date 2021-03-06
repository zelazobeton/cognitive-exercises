package com.zelazobeton.cognitiveexercieses.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.zelazobeton.cognitiveexercieses.model.GameDataDto;
import com.zelazobeton.cognitiveexercieses.repository.GameDataRepository;

@Service
public class GameDataDataServiceImpl implements GameDataService {
    GameDataRepository gameDataRepository;

    public GameDataDataServiceImpl(GameDataRepository gameDataRepository) {
        this.gameDataRepository = gameDataRepository;
    }

    @Override
    public List<GameDataDto> getGamesData() {
        return gameDataRepository.findAll().stream().map(GameDataDto::new).collect(Collectors.toList());
    }
}
