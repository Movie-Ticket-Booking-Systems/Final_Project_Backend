package com.app.services;

import com.app.Exceptions.TheaterIsNotPresentOnThisAddress;
import com.app.Exceptions.TheaterIsPresentOnThatAddress;
import com.app.Repositories.TheaterRepository;
import com.app.Repositories.UserRepository;
import com.app.RequestDtos.TheaterEntryDto;
import com.app.RequestDtos.TheaterSeatEntryDto;
import com.app.ResponseDtos.TheaterResponseDto;
import com.app.Transformers.TheaterTransformer;
import com.app.models.Theater;
import com.app.models.TheaterSeat;
import com.app.models.User;
import com.app.types.SeatType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;
@Transactional
@Service
public class TheaterService {
    @Autowired
    private TheaterRepository theaterRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    
    
    public String addTheater(TheaterEntryDto theaterEntryDto) throws TheaterIsPresentOnThatAddress{
        if(theaterRepository.getTheaterAndTheaterSeatDetails(theaterEntryDto.getAddress()) != null) {
            throw new TheaterIsPresentOnThatAddress();
        }
        Theater theater = TheaterTransformer.theaterDtoToTheater(theaterEntryDto);
        
        
        theaterRepository.save(theater);
        return "Theater has been saved Successfully";
    }
  //changes by saswat
    public String addTheaterSeat(TheaterEntryDto entryDto) throws TheaterIsNotPresentOnThisAddress{
        if(theaterRepository.getTheaterAndTheaterSeatDetails(entryDto.getAddress()) == null) {
            throw new TheaterIsNotPresentOnThisAddress();
        }
        Integer noOfSeatsInRow = entryDto.getNoOfSeatInRow();
        Integer noOfPremiumSeats = entryDto.getNoOfPremiumSeat();
        Integer noOfClassicSeat = entryDto.getNoOfClassicSeat();
        String address = entryDto.getAddress();
        System.out.println(address);

        Theater theater = theaterRepository.getTheaterAndTheaterSeatDetails(address);
        List<TheaterSeat> seatList = theater.getTheaterSeatList();

        int counter = 1;
        int fill = 0;
        char ch = 'A';

        for(int i = 1; i <= noOfClassicSeat; i++) {
            String seatNo = Integer.toString(counter)+ch;

            ch++;
            fill++;
            if(fill == noOfSeatsInRow) {
                fill = 0;
                counter++;
                ch = 'A';
            }

            TheaterSeat theaterSeat = new TheaterSeat();
            theaterSeat.setSeatNo(seatNo);
            theaterSeat.setSeatType(SeatType.CLASSIC);
            theaterSeat.setTheater(theater);
            seatList.add(theaterSeat);
        }

        for(int i = 1; i <= noOfPremiumSeats; i++) {
            String seatNo = Integer.toString(counter)+ch;

            ch++;
            fill++;
            if(fill == noOfSeatsInRow) {
                fill = 0;
                counter++;
                ch = 'A';
            }

            TheaterSeat theaterSeat = new TheaterSeat();
            theaterSeat.setSeatNo(seatNo);
            theaterSeat.setSeatType(SeatType.PREMIUM);
            theaterSeat.setTheater(theater);
            seatList.add(theaterSeat);
        }
        theater.setTheaterSeatList(seatList);
        theaterRepository.save(theater);

        return "Theater Seats have been added successfully";
    }
    
    public List<TheaterResponseDto> getTheaterList(){
    	List<Theater> list=theaterRepository.findAll();
    	List<TheaterResponseDto> rlist=TheaterTransformer.theaterToTheaterDto(list);
    	return rlist;
    }
    
    
    public TheaterResponseDto getTheater(Integer id )
    {
    	List<Theater> list=new ArrayList<Theater>();
    	list.add(theaterRepository.findById(id).orElseThrow());
    	List<TheaterResponseDto> rlist=TheaterTransformer.theaterToTheaterDto(list);
    	if(!rlist.isEmpty())
    		return rlist.get(0);
    	else
    		return null;
    }
    
    
    public String deleteTheater(Integer id) throws UsernameNotFoundException{
    	Theater t=theaterRepository.findById(id).get();
    	if(t==null)throw new UsernameNotFoundException("User Does Not Exist");
    	theaterRepository.deleteById(id);
    	return "Theater Deleted Successfully";
    	
    }
}
