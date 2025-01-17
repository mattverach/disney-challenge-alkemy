package com.alkemy.disney.service.impl;
import com.alkemy.disney.mapper.CharacterMapper;
import com.alkemy.disney.dto.MovieDetailDTO;
import com.alkemy.disney.dto.MovieListDTO;
import com.alkemy.disney.dto.MovieUpdateDTO;
import com.alkemy.disney.entity.CharacterEntity;
import com.alkemy.disney.entity.GenreEntity;
import com.alkemy.disney.entity.MovieEntity;
import com.alkemy.disney.exception.ApiRequestException;
import com.alkemy.disney.mapper.MovieMapper;
import com.alkemy.disney.repository.CharacterRepository;
import com.alkemy.disney.repository.MovieRepository;
import com.alkemy.disney.service.MovieService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MovieServiceImpl implements MovieService {
    @Autowired
    MovieRepository movieRepository;
    @Autowired
    MovieMapper movieMapper;
    @Autowired
    CharacterRepository characterRepository;
    @Autowired
    private CharacterMapper characterMapper;

    ModelMapper modelMapper = new ModelMapper();

    public List<MovieListDTO> getAllMovies() {
        List<MovieEntity> moviesEntities = movieRepository.findAll();
        List<MovieListDTO> moviesDTO = new ArrayList<>();
        for (MovieEntity m : moviesEntities) {
            MovieListDTO mDTO = modelMapper.map(m, MovieListDTO.class);
            moviesDTO.add(mDTO);
        }
        return moviesDTO;
    }

    public MovieDetailDTO getMovieById(Long id) {
        Optional<MovieEntity> movieEntity = movieRepository.findById(id);
        if (!movieEntity.isPresent()) {
            throw new RuntimeException("Movie with id " + id + " not found");
        }
        MovieDetailDTO movieDTO = movieMapper.movieEntityToDTO(movieEntity.get());
        return movieDTO;

    }

    public MovieDetailDTO saveMovie(MovieDetailDTO movie) {
        MovieEntity movieEntity = movieMapper.movieDTOToEntity(movie);
        MovieEntity savedMovie = movieRepository.save(movieEntity);
        MovieDetailDTO savedMovieDTO = movieMapper.movieEntityToDTO(savedMovie);
        return savedMovieDTO;
    }

    @Override
    public void deleteMovie(Long id) {
        Optional<MovieEntity> movie = movieRepository.findById(id);
        if (!movie.isPresent()) {
            throw new RuntimeException("Movie with id " + id + " not found");
        }
        movieRepository.deleteById(id);
    }

    @Override
    public MovieDetailDTO updateMovie(Long id, MovieUpdateDTO movieDTO) {


        Optional<MovieEntity> movieEntity = movieRepository.findById(id);

        if (!movieEntity.isPresent()) {
            throw new RuntimeException("Movie with id " + id + " not found");
        }
        MovieEntity movieToUpdate = movieEntity.get();

        MovieEntity updatedMovie = movieRepository.save(movieMapper.updateMovieMapper(movieDTO, movieToUpdate));
        return movieMapper.movieEntityToDTO(updatedMovie);
    }

    @Override
    public MovieDetailDTO postCharacter(Long id, Long idCharacter) {
        Optional<MovieEntity> movieEntity = movieRepository.findById(id);
        if (!movieEntity.isPresent()) {
            throw new RuntimeException("Movie with id " + id + " not found");
        }
        MovieEntity movieToAddCharacters = movieEntity.get();
        Optional<CharacterEntity> characterEntity = characterRepository.findById(idCharacter);
        if(!characterEntity.isPresent()){
            throw new RuntimeException(("Character with id " + idCharacter + " not found"));
        }
        CharacterEntity characterToAddInMovie = characterEntity.get();
        if(movieToAddCharacters.getCharacters().contains(characterEntity)){
            // Excepción el personaje ya está en la película
        }
        movieToAddCharacters.addCharacter(characterToAddInMovie);
        characterToAddInMovie.addMovie(movieToAddCharacters);
        movieRepository.save(movieToAddCharacters);
        characterRepository.save(characterToAddInMovie);

        return movieMapper.movieEntityToDTO(movieToAddCharacters);
    }

    @Override
    public void deleteCharacter(Long id, Long idCharacter) {
        Optional<MovieEntity> movieEntity = movieRepository.findById(id);
        if (!movieEntity.isPresent()) {
            throw new RuntimeException("Movie with id " + id + " not found");
        }
        MovieEntity movieToRemoveCharacters = movieEntity.get();
        Optional<CharacterEntity> characterEntity = characterRepository.findById(idCharacter);
        if(!characterEntity.isPresent()){
            throw new RuntimeException(("Character with id " + idCharacter + " not found"));
        }
        CharacterEntity characterToRemoveInMovie = characterEntity.get();
        movieToRemoveCharacters.removeCharacter(characterToRemoveInMovie);
        characterToRemoveInMovie.removeMovie(movieToRemoveCharacters);
        movieRepository.save(movieToRemoveCharacters);
        characterRepository.save(characterToRemoveInMovie);
    }


}
