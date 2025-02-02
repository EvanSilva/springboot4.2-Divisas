package edu.badpals.cambioDivisas.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
public class DivisaController {

    private final RestTemplate restTemplate = new RestTemplate();


    @RequestMapping("/cambio")
    public String cargarPagina(Model model) {

        return "cambio";
    }

    private double obtenerTasaCambio(String jsonResponse, String monedaDestino) {
        try {

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonResponse);


            return rootNode.path("rates").path(monedaDestino).asDouble();
        } catch (Exception e) {
            e.printStackTrace();
            return 1.0;
        }
    }

    @PostMapping("/cambioDivisas")
    public String cambioDivisa(@RequestParam("importe") double importe,
                               @RequestParam("moneda-origen") String base,
                               @RequestParam("moneda-destino") String symbols,
                               Model model) {

        String url = "https://api.frankfurter.app/latest?base=" + base + "&symbols=" + symbols;

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        double tasaCambio = obtenerTasaCambio(response.getBody(), symbols);
        double importeConvertido = importe * tasaCambio;

        System.out.println("Tasa de cambio: " + tasaCambio);
        System.out.println("Importe convertido: " + importeConvertido);

        model.addAttribute("importeConvertido", importeConvertido);

        return "cambio";
    }


}
