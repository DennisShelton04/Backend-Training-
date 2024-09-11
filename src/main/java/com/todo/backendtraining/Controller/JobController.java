package com.todo.backendtraining.Controller;

import com.todo.backendtraining.Entity.Job;
import com.todo.backendtraining.Service.JobService;

import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")
public class JobController {
  private final JobService jobService;

  public JobController(JobService jobService) {
    this.jobService = jobService;
  }

  @GetMapping
  public List<Job> getAllJobs() {
    return jobService.getAllJobs();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Job> getJobById(@PathVariable Long id) {
    Optional<Job> job = Optional.ofNullable(jobService.getJobById(id));
    return job.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<Job> createJob(@RequestBody Job job) {
    Job createdJob = jobService.createOrUpdateJob(job);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdJob);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Job> updateJob(@PathVariable Long id, @RequestBody Job job) {
    job.setId(id);
    Job updatedJob = jobService.createOrUpdateJob(job);
    return ResponseEntity.ok(updatedJob);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
    jobService.deleteJob(id);
    return ResponseEntity.noContent().build();
  }
}
