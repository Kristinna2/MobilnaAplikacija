package pages



//import data.MarkerData
/*
@Composable
fun DetailsPage(navController: NavController) {
    val eventViewModel: EventViewModel = viewModel()

    // Učitavanje događaja
    LaunchedEffect(Unit) {
        eventViewModel.loadAllEvents()
    }

    val eventData by eventViewModel.eventData.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        eventData?.let { event ->
            Text(
                text = "Naziv događaja: ${event.eventName}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Opis: ${event.description}", fontSize = 16.sp)

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Tip događaja: ${event.eventType}", fontSize = 16.sp)

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Nivo gužve: ${event.crowdLevel}", fontSize = 16.sp)

            event.location?.let { location ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Lokacija: ${location.latitude}, ${location.longitude}",
                    fontSize = 16.sp
                )
            }

            event.mainImage?.let { uri ->
                if (uri.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(
                        painter = rememberImagePainter(uri),
                        contentDescription = "Glavna slika događaja",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                } else {
                    // Prikaz podrazumevane slike
                    Image(
                        painter = rememberImagePainter("URL_TO_DEFAULT_IMAGE"),
                        contentDescription = "Podrazumevana slika događaja",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }

          /*  event.galleryImages?.let { images ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Galerija slika:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                LazyRow {
                    items(images) { imageUri ->
                        Image(
                            painter = rememberImagePainter(imageUri),
                            contentDescription = "Slika iz galerije",
                            modifier = Modifier
                                .size(100.dp)
                                .padding(4.dp)
                        )
                    }
                }
            }*/
        } ?: run {
            Text(text = "Podaci nisu dostupni.", fontSize = 16.sp)
        }
    }
}*/
