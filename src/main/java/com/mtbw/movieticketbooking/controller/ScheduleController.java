package com.mtbw.movieticketbooking.controller;

import com.mtbw.movieticketbooking.dto.CityOption;
import com.mtbw.movieticketbooking.dto.DateTab;
import com.mtbw.movieticketbooking.dto.MovieSchedule;
import com.mtbw.movieticketbooking.entity.Cinema;
import com.mtbw.movieticketbooking.entity.CinemaChain;
import com.mtbw.movieticketbooking.service.CinemaService;
import com.mtbw.movieticketbooking.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * UC03 - Movie Schedule (Lich chieu theo khu vuc / rap / ngay)
 * Toan bo lua chon (khu vuc, rap, phim, suat chieu) deu lay truc tiep tu du lieu
 * that trong database - khong hardcode.
 */
@Controller
@RequiredArgsConstructor
public class ScheduleController {

    private static final String[] DOW_LABEL = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "CN"};

    private final CinemaService cinemaService;
    private final ShowtimeService showtimeService;

    @GetMapping("/showtime")
    public String schedule(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Long cinemaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {

        LocalDate selectedDate = (date != null) ? date : LocalDate.now();

        // 1) Danh sach khu vuc kem so luong rap (badge) - lay tu DB that
        List<CityOption> cityOptions = cinemaService.findCityOptions();
        String selectedCity = (city != null && !city.isBlank())
                ? city
                : (cityOptions.isEmpty() ? null : cityOptions.get(0).city());

        // 2) Danh sach rap theo khu vuc, gom theo he thong rap (chain) that co trong DB
        List<Cinema> cinemas = (selectedCity != null) ? cinemaService.findByCity(selectedCity) : List.of();
        LinkedHashMap<CinemaChain, List<Cinema>> cinemasByChain = cinemaService.groupByChain(cinemas);

        // 3) Rap dang chon
        Cinema selectedCinema = null;
        if (cinemaId != null) {
            selectedCinema = cinemaService.findById(cinemaId).orElse(null);
        }
        if (selectedCinema == null && !cinemas.isEmpty()) {
            selectedCinema = cinemas.get(0);
        }

        // 4) Lich chieu (da format san gio/gia/thoi luong) cho rap + ngay da chon
        List<MovieSchedule> schedule = (selectedCinema != null)
                ? showtimeService.getScheduleByCinemaAndDate(selectedCinema.getId(), selectedDate)
                : List.of();

        model.addAttribute("cityOptions", cityOptions);
        model.addAttribute("selectedCity", selectedCity);
        model.addAttribute("cinemasByChain", cinemasByChain);
        model.addAttribute("selectedCinema", selectedCinema);
        model.addAttribute("dateList", buildNext7Days());
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("schedule", schedule);

        return "schedule";
    }

    private List<DateTab> buildNext7Days() {
        LocalDate today = LocalDate.now();
        List<DateTab> list = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate d = today.plusDays(i);
            String label = (i == 0) ? "Hôm nay" : DOW_LABEL[d.getDayOfWeek().getValue() - 1];
            list.add(new DateTab(d, label));
        }
        return list;
    }
}