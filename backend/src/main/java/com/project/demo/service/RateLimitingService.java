package com.project.demo.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitingService {
    // Lưu trữ bucket của từng User ID
    private final Map<Long, Bucket> cache = new ConcurrentHashMap<>();

    // Lấy ra bucket của user, nếu chưa có thì tạo mới
    public Bucket resolveBucket(Long userId) {
        return cache.computeIfAbsent(userId, this::newBucket);
    }

    private Bucket newBucket(Long userId) {
        // Cấu hình: Tối đa 3 requests. Nạp lại 3 requests mỗi 10 giây.
        // Tức là nếu user bấm điên cuồng, từ cái bấm thứ 4 trở đi trong vòng 10 giây sẽ bị chặn.
        Bandwidth limit = Bandwidth.classic(3, Refill.greedy(3, Duration.ofSeconds(10)));
        return Bucket.builder().addLimit(limit).build();
    }
}
