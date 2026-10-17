package com.lostlink.backend.controller;

import com.lostlink.backend.entity.Item;
import com.lostlink.backend.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "http://localhost:5173")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        return ResponseEntity.ok(itemService.getAllItems());
    }

    @GetMapping("/mine")
    public ResponseEntity<List<Item>> getMyReports(Authentication authentication) {
        return ResponseEntity.ok(itemService.getMyReports(authentication.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        return itemService.getItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Item> createItem(
            @Valid @RequestBody Item item,
            Authentication authentication) {
        Item savedItem = itemService.createItem(item, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody Item item) {
        try {
            return ResponseEntity.ok(itemService.updateItem(id, item));
        } catch (RuntimeException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        if (itemService.getItemById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}