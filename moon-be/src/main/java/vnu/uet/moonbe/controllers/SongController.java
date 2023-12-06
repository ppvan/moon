package vnu.uet.moonbe.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vnu.uet.moonbe.dto.DetailSongDto;
import vnu.uet.moonbe.repositories.SongRepository;
import vnu.uet.moonbe.services.SongService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/songs")
@RequiredArgsConstructor
public class SongController {

	private final SongService songService;

	private final SongRepository songRepository;

	@GetMapping("/all")
	public ResponseEntity<List<DetailSongDto>> getAllSongs() {
		List<DetailSongDto> songs = songService.getAllSongs();
		return new ResponseEntity<>(songs, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<DetailSongDto> getSongDetail(@PathVariable int id) {
		DetailSongDto detailSongDTO = songService.getSongById(id);
		return new ResponseEntity<>(detailSongDTO, HttpStatus.OK);
	}

	@GetMapping("/search/title")
	public ResponseEntity<List<DetailSongDto>> getSongsByTitle(@RequestParam String title) {
		List<DetailSongDto> songs = songService.getSongsByTitle(title);
		return new ResponseEntity<>(songs, HttpStatus.OK);
	}

	@GetMapping("/search/artist")
	public ResponseEntity<List<DetailSongDto>> getSongsByArtist(@RequestParam String artist) {
		List<DetailSongDto> songs = songService.getSongsByArtist(artist);
		return new ResponseEntity<>(songs, HttpStatus.OK);
	}

	@GetMapping("/search/genre")
	public ResponseEntity<List<DetailSongDto>> getSongsByGenre(@RequestParam String genre) {
		List<DetailSongDto> songs = songService.getSongsByGenre(genre);
		return new ResponseEntity<>(songs, HttpStatus.OK);
	}

	@PostMapping("/upload")
	public ResponseEntity<?> uploadSong(
			@RequestPart(name = "title") String title,
			@RequestPart(name = "artist") String artist,
			@RequestPart(name = "genre") String genre,
			@RequestPart("file")MultipartFile file
			) {
		DetailSongDto detailSongDto = new DetailSongDto(title, artist, genre);
		return songService.uploadSong(detailSongDto, file);
	}

	@GetMapping("/{id}/download")
	public ResponseEntity<FileSystemResource> downloadSong(
			@PathVariable int id
	) {
		return songService.downloadSong(id);
	}

	@PutMapping("/{id}/update")
	public ResponseEntity<?> updateSong(
			@PathVariable int id,
			@RequestBody DetailSongDto detailSongDTO
	) {
		return songService.updateSong(id, detailSongDTO);
	}

	@DeleteMapping("/{id}/delete")
	public ResponseEntity<?> deleteSong(@PathVariable int id) {
		return songService.deleteSong(id);
	}

	@GetMapping("/suggestions")
	public ResponseEntity<List<String>> getSearchSuggestions(
			@RequestParam String keyword
	) {
		List<String> suggestionsTitle = songRepository.findSuggestionsTitle(keyword);
		List<String> suggestionsArtist = songRepository.findSuggestionsArtist(keyword);
		List<String> suggestionsGenre = songRepository.findSuggestionsGenre(keyword);

		List<String> allSuggestions = new ArrayList<>();

		allSuggestions.addAll(suggestionsTitle);
		allSuggestions.addAll(suggestionsArtist);
		allSuggestions.addAll(suggestionsGenre);

		return ResponseEntity.ok(allSuggestions);
	}
}