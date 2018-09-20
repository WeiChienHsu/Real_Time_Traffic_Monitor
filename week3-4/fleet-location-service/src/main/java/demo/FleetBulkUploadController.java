package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FleetBulkUploadController {

    private LocationRepository repository;

    @Autowired
    public FleetBulkUploadController(LocationRepository repository) {
        this.repository = repository;

    }

    @RequestMapping(value = "/fleet", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void upload(@RequestBody List<Location> cars) throws Exception {
        this.repository.save(cars);
    }

    @RequestMapping(value = "/purge", method = RequestMethod.POST)
    public void purge() {
        this.repository.deleteAll();
    }

}