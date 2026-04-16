package com.start_up_insight_engine.service;

import com.start_up_insight_engine.repository.AddOnRepository;
import com.start_up_insight_engine.transport.AddOnAddedEvent;
import com.start_up_insight_engine.transport.AddOnRemovedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddOnService {

    @Autowired
    private AddOnRepository addOnRepository;

    public void handleAddonAdded(AddOnAddedEvent event) {

    }

    public void handleAddonRemoved(AddOnRemovedEvent event) {

    }
}
