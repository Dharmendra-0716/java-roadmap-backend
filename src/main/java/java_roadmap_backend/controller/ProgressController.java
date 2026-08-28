package java_roadmap_backend.controller;

import java_roadmap_backend.entity.User;
import java_roadmap_backend.entity.UserProgress;
import java_roadmap_backend.repository.UserProgressRepository;
import java_roadmap_backend.repository.UserRepository;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/progress")
@CrossOrigin(origins = "*")
public class ProgressController {

    private final UserRepository userRepository;
    private final UserProgressRepository progressRepository;

    public ProgressController(
            UserRepository userRepository,
            UserProgressRepository progressRepository) {

        this.userRepository = userRepository;
        this.progressRepository = progressRepository;
    }

    @GetMapping("/{email}")
    public UserProgress getProgress(@PathVariable String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return progressRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    UserProgress progress = new UserProgress();
                    progress.setUser(user);
                    progress.setProgress("{}");
                    return progressRepository.save(progress);
                });
    }

    @PostMapping("/{email}")
    public UserProgress saveProgress(
            @PathVariable String email,
            @RequestBody String progress) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProgress userProgress = progressRepository
                .findByUserId(user.getId())
                .orElseGet(UserProgress::new);

        userProgress.setUser(user);
        userProgress.setProgress(progress);

        return progressRepository.save(userProgress);
    }
}