package com.ka.recipieapi.api;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.NoSuchElementException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ka.recipieapi.types.BaseRecipe;
import com.ka.recipieapi.types.RecipeDto;

@RestController
public class RecipieController {
    private OpenAIService openAIService;
    private ImageDownloadService imageDownloadService;
    private RecipeCollectorService recipeCollectorService;

    public RecipieController(OpenAIService openAIService, RecipeCollectorService recipeCollectorService, ImageDownloadService imageDownloadService) {
        this.openAIService = openAIService;
        this.recipeCollectorService = recipeCollectorService;
        this.imageDownloadService = imageDownloadService;
    }


    @GetMapping("api/recipe")
    @CrossOrigin(origins = "${cors.allowed.origins}")
    public RecipeDto getCompletion(@RequestParam String url) {
        System.out.println("url: " + url);
        BaseRecipe recipe;
        try {
            return recipeCollectorService.getRecipeFromUrl(url);
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
            recipe = openAIService.getCompletion(recipeCollectorService.getPlainTextFromUrl(url));
        } catch (Exception e){
            e.printStackTrace();
            throw new NoSuchElementException();
        }

        String imageUrl = "NoImage";
        try {
            if(recipe != null) {
                imageUrl = recipeCollectorService.getImageWithAltFromMain(url,recipe.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        RecipeDto recipeDto = recipe.toRecipeDto(imageUrl);

        return recipeDto;
    }

    @PostMapping("api/download-image")
    @CrossOrigin(origins = "${cors.allowed.origins}")
    public ResponseEntity<byte[]> downloadImage(@RequestBody String imageUrl) {
        try {
            Path tempFilePath;
            tempFilePath = Files.createTempFile("downloaded-image", ".jpg");
            File downloadedFile = imageDownloadService.downloadImage(imageUrl, tempFilePath.toString());

            Resource fileResource;
            fileResource = new UrlResource(downloadedFile.toURI());
            byte[] imageBytes = Files.readAllBytes(tempFilePath);

            return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadedFile.getName() + "\"")
                .body(imageBytes);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("NoImage".getBytes(StandardCharsets.UTF_8));
        }
    }

    @GetMapping("api/recipe/mock")
    @CrossOrigin(origins = "${cors.allowed.origins}")
    public void getCompletionMock(@RequestParam String url) {
        String recipeAsString = "{\n"
            + "  \"name\": \"Lasagne\",\n"
            + "  \"portions\": 4,\n"
            + "  \"ingredients\": [\n"
            + "    \"9 stk. lasagneplater\",\n"
            + "    \"3 dl revet hvitost til toppen\",\n"
            + "    \"Kjøttsaus:\",\n"
            + "    \"150 g bacon eller pancetta\",\n"
            + "    \"2 ss nøytral olje\",\n"
            + "    \"400 g kjøttdeig\",\n"
            + "    \"1 stk. finhakket løk\",\n"
            + "    \"1 stk. finhakket gulrot\",\n"
            + "    \"1 stilk finhakket stilkselleri (stangselleri)\",\n"
            + "    \"1 ss nøytral olje\",\n"
            + "    \"2 ss tomatpuré\",\n"
            + "    \"2 bokser hermetiske tomater (á 400 g)\",\n"
            + "    \"ca. 2 dl kjøttkraft , buljong eller vann\",\n"
            + "    \"2 ts tørket oregano\",\n"
            + "    \"Ostesaus:\",\n"
            + "    \"3 ss smør\",\n"
            + "    \"3 ss hvetemel\",\n"
            + "    \"6 dl melk\",\n"
            + "    \"5 ss revet parmesan eller annen hvit ost\",\n"
            + "    \"1 ts salt\",\n"
            + "    \"0,5 ts kvernet pepper\",\n"
            + "    \"0,5 ts revet muskatnøtt\"\n"
            + "  ],\n"
            + "  \"instructions\": [\n"
            + "    \"1. Skjær bacon i små biter. Ha olje i en varm panne og stek til baconet til det er gyllent. Ha i litt mer olje og brun kjøttdeig i olje på sterk varme i to omganger.\",\n"
            + "    \"2. Senk varmen litt og ha i løk, gulrot og stilkselleri og la det steke til grønnsakene er blitt myke og blanke. Ha baconet og kjøttdeigen tilbake i stekepanna og bland alt sammen.\",\n"
            + "    \"3. Lag en grop i midten av stekepannen. Ha i litt mer olje og fres tomat puréen i noen minutter. Ha i hermetisk tomat, kraft og krydder. La kjøttsausen småkoke i minst 10 minutter, til den begynner å tykne. Har du litt god tid? La gjerne sausen småkoke under lokk i ca. 1 ½ time, for å utvikle ekstra god smak. Husk å rør litt i kjelen underveis, slik at det ikke fester seg på bunnen av gryta. Smak til med salt og pepper.\",\n"
            + "    \"4. Smelt smør i en kjele og rør inn mel. Spe med melk under omrøring og la sausen koke i ca. 10 minutter. Den skal være forholdsvis tykk. Ha i parmesan og la osten smelte. Smak til ostesausen med krydder.\",\n"
            + "    \"5. Legg kjøttsaus, pastaplater og ostesaus lagvis i en ildfast form. Avslutt med pastaplater og ostesaus. Dryss over revet ost til slutt.\",\n"
            + "    \"6. Sett formen i stekeovn på 200° C og stek i 30-40 minutter. Kjenn etter med en pinne eller spiss kniv om pastaen er mør. La lasagnen hvile noen minutter før servering.\"\n"
            + "  ]\n"
            + "}\n";

            ObjectMapper objectMapper = new ObjectMapper();
            BaseRecipe recipe = null;
            String imageUrl = "Picture not found";
            try {
                recipe = objectMapper.readValue(recipeAsString, BaseRecipe.class);
                if(recipe != null) {
                    imageUrl = recipeCollectorService.getImageWithAltFromMain(url,recipe.getName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

//            RecipeDto recipeDto = recipe.toRecipeDto(imageUrl);
//            System.out.println(recipeDto);

//            return recipeDto;
    }

    @GetMapping("api/image")
    @CrossOrigin(origins = "${cors.allowed.origins}")
    public String getImage(@RequestParam String url, @RequestParam String title) {
        try {
            return recipeCollectorService.getImageWithAltFromMain(url,title);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(value = "/api/ping")
    public String getRecipeFromURL(){
        return "This will be a recipe in the future";
    }
}
