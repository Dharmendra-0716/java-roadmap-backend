package java_roadmap_backend.controller;

import java_roadmap_backend.entity.User;
import java_roadmap_backend.entity.UserProgress;
import java_roadmap_backend.repository.UserProgressRepository;
import java_roadmap_backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> getProgress(
            @PathVariable String email,
            @RequestAttribute(value = "authenticatedEmail", required = false)
            String authenticatedEmail) {

        if (authenticatedEmail == null ||
                !authenticatedEmail.equalsIgnoreCase(email)) {

            return ResponseEntity
                    .status(401)
                    .body("Unauthorized");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(
                progressRepository.findByUserId(user.getId())
                        .orElseGet(() -> {
                            UserProgress progress = new UserProgress();
                            progress.setUser(user);
                            progress.setProgress("{}");
                            return progressRepository.save(progress);
                        })
        );
    }

    @PostMapping("/{email}")
    public ResponseEntity<?> saveProgress(
            @PathVariable String email,
            @RequestBody String progress,
            @RequestAttribute(value = "authenticatedEmail", required = false)
            String authenticatedEmail) {

        if (authenticatedEmail == null ||
                !authenticatedEmail.equalsIgnoreCase(email)) {

            return ResponseEntity
                    .status(401)
                    .body("Unauthorized");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProgress userProgress = progressRepository
                .findByUserId(user.getId())
                .orElseGet(UserProgress::new);

        userProgress.setUser(user);
        userProgress.setProgress(progress);

        return ResponseEntity.ok(
                progressRepository.save(userProgress)
        );
    }
}