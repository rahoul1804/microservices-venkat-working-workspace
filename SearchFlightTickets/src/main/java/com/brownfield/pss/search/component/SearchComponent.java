package com.brownfield.pss.search.component;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brownfield.pss.search.entity.Flight;
import com.brownfield.pss.search.entity.Inventory;
import com.brownfield.pss.search.repository.FlightRepository;

@Service
public class SearchComponent {
	private FlightRepository flightRepository;
	private static final Logger logger = LoggerFactory.getLogger(SearchComponent.class);
	
	
	@Autowired
	public SearchComponent(FlightRepository flightRepository){
		this.flightRepository = flightRepository;
	}

	public List<Flight> search(SearchQuery query){
		List<Flight> flights= flightRepository.findByOriginAndDestinationAndFlightDate(query.getOrigin(),
																query.getDestination(),
																query.getFlightDate()); 
		List<Flight> searchResult = new ArrayList<Flight>();
		searchResult.addAll(flights);
		flights.forEach(flight -> {
										flight.getFares();
										int inv = flight.getInventory().getCount();
										if (inv < 0) {
											searchResult.remove(flight);
										}
								    }
					    );
		return searchResult; 
	}

	public void updateInventory(String flightNumber, String flightDate, int new_inventory) {
		logger.info("Updating inventory for flight "+ flightNumber + " innventory "+ new_inventory); 
		Flight flight = flightRepository.findByFlightNumberAndFlightDate(flightNumber,flightDate);
		if(flight != null){ //added by Ramesh
			Inventory inv = flight.getInventory();
			inv.setCount(new_inventory);
			flightRepository.save(flight);
		}else{ //flight info is not available yet in search schema
			throw new ListenerExecutionFailedException("", new Throwable());
		} 
	}	 
}





