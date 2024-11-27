package com.App.Yogesh.Repository;

import com.App.Yogesh.Models.Reels;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReelsRepository extends JpaRepository<Reels ,Integer> {
    public List<Reels> findByUserId(Integer userId);

    int countByUserId(Integer userId);
}
