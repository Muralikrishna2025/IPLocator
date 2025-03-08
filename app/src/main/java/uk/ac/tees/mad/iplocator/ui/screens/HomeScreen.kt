package uk.ac.tees.mad.iplocator.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.iplocator.model.dataclass.IpLocation
import uk.ac.tees.mad.iplocator.model.dataclass.Language
import uk.ac.tees.mad.iplocator.model.dataclass.Location
import uk.ac.tees.mad.iplocator.navigation.Dest
import uk.ac.tees.mad.iplocator.ui.utils.IpLocatorTopAppBar
import uk.ac.tees.mad.iplocator.viewmodel.HomeScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController, viewmodel: HomeScreenViewModel = koinViewModel()
) {
    val deviceIp by viewmodel.deviceIp.collectAsStateWithLifecycle()
    val ipLocationDetails by viewmodel.ipLocation.collectAsStateWithLifecycle()
    val language = Language(
        code = "en", name = "English", native = "English"
    )

    val location = Location(
        geonameId = 5368361,
        capital = "Washington D.C.",
        languages = listOf(language),
        countryFlag = "https://assets.ipstack.com/images/assets/flags_svg/us.svg",
        countryFlagEmoji = "🇺🇸",
        countryFlagEmojiUnicode = "U+1F1FA U+1F1F8",
        callingCode = "1",
        isEu = false
    )

//    val timeZone = TimeZone(
//        id = "America/Los_Angeles",
//        currentTime = "2018-03-29T07:35:08-07:00",
//        gmtOffset = -25200,
//        code = "PDT",
//        isDaylightSaving = true
//    )
//
//    val currency = Currency(
//        code = "USD",
//        name = "US Dollar",
//        plural = "US dollars",
//        symbol = "$",
//        symbolNative = "$"
//    )
//
//    val connection = Connection(
//        asn = 25876,
//        isp = "Los Angeles Department of Water",
//        sld = "ladwp",
//        tld = "com",
//        carrier = "los angeles department of water",
//        home = null,
//        organizationType = null,
//        isicCode = null,
//        naicsCode = null
//    )

    var ipLocation = IpLocation(
        ip = deviceIp,
        type = "ipv4",
        continentCode = "NA",
        continentName = "North America",
        countryCode = "US",
        countryName = "United States",
        regionCode = "CA",
        regionName = "California",
        city = "Los Angeles",
        zip = "90013",
        latitude = 34.0655,
        longitude = -118.2405,
        msa = "31100",
        dma = "803",
        radius = null,
        ipRoutingType = null,
        connectionType = null,
        location = location,

//        timeZone = timeZone,
//        currency = currency,
//        connection = connection

    )

//    if (deviceIp != null) {
//        if(ipLocationDetails==null){
//        viewmodel.getIpLocationDetails(deviceIp!!)}
//        if (ipLocationDetails != null) {
//            ipLocation = ipLocationDetails!!
//        }
//    } else {
//        ipLocation = IpLocation(
//            ip = "134.201.250.155",
//            type = "ipv4",
//            continentCode = "NA",
//            continentName = "North America",
//            countryCode = "US",
//            countryName = "United States",
//            regionCode = "CA",
//            regionName = "California",
//            city = "Los Angeles",
//            zip = "90013",
//            latitude = 34.0655,
//            longitude = -118.2405,
//            msa = "31100",
//            dma = "803",
//            radius = null,
//            ipRoutingType = "fixed",
//            connectionType = "ipv4",
//            location = location,
//
////        timeZone = timeZone,
////        currency = currency,
////        connection = connection
//
//        )
//    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            IpLocatorTopAppBar(
                title = "IP Locator",
                scrollBehavior = scrollBehavior,
                navController = navController
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = { navController.navigate(Dest.MapScreen) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Explore,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                text = { Text("Go to Map Screen") })
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->


        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(400.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item(
                span = StaggeredGridItemSpan.FullLine
            ) {
                HeaderCard(ipLocation)
            }
            item { LocationDetail(ipLocation) }
            item { ISPDetail(ipLocation) }
//            item { TimezoneDetail(ipLocation) }
//            item { CurrencyDetail(ipLocation) }
            item(
                span = StaggeredGridItemSpan.FullLine
            ) { Spacer(modifier = Modifier.height(64.dp)) }
        }

    }

}

@Composable
fun HeaderCard(ipLocation: IpLocation) {
    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "IP Address: ${ipLocation.ip}",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(8.dp), thickness = 2.dp
        )
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "$label:", fontWeight = FontWeight.Bold)
        Text(text = value)
    }
}

@Composable
fun LocationDetail(ipLocation: IpLocation) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "Location Details",
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Location Details",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.titleSmall
            )
        }
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                DetailRow(label = "City", value = ipLocation.city.toString())
                DetailRow(
                    label = "Region",
                    value = "${ipLocation.regionName} (${ipLocation.regionCode})"
                )
                DetailRow(
                    label = "Country",
                    value = "${ipLocation.countryName} (${ipLocation.countryCode})"
                )
                DetailRow(
                    label = "Continent",
                    value = "${ipLocation.continentName} (${ipLocation.continentCode})"
                )
                DetailRow(label = "Postal Code", value = ipLocation.zip.toString())
                Text(text = "Coordinates", fontWeight = FontWeight.Bold, modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 8.dp))
                DetailRow(
                    label = "Latitude",
                    value = "${ipLocation.latitude}"
                )
                DetailRow(
                    label = "Longitude",
                    value = "${ipLocation.longitude}"
                )
                DetailRow(label = "Capital", value = ipLocation.location?.capital.toString())
                DetailRow(label = "Calling Code", value = "+${ipLocation.location?.callingCode}")
                DetailRow(
                    label = "Flag",
                    value = "${ipLocation.location?.countryFlagEmoji} ${ipLocation.location?.countryFlagEmojiUnicode}"
                )
            }
        }
    }
}

@Composable
fun ISPDetail(ipLocation: IpLocation) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.NetworkCheck,
                contentDescription = "ISP Details",
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Network Details",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.titleSmall
            )
        }
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                //DetailRow(label = "ISP", value = ipLocation.connection.isp)
                //DetailRow(label = "ASN", value = ipLocation.connection.asn.toString())
                //DetailRow(label = "Domain", value = "${ipLocation.connection.sld}.${ipLocation.connection.tld}")
                //DetailRow(label = "Carrier", value = ipLocation.connection.carrier)
                DetailRow(label = "IP Type", value = ipLocation.type.toString())
                DetailRow(label = "IP Routing Type", value = ipLocation.ipRoutingType.toString())
            }
        }
    }
}
//
//@Composable
//fun TimezoneDetail(ipLocation: IpLocation) {
//    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 8.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Icon(
//                imageVector = Icons.Default.Schedule,
//                contentDescription = "Timezone Details",
//                tint = MaterialTheme.colorScheme.primary
//            )
//            Text(
//                text = "Timezone Details",
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.padding(start = 8.dp),
//                style = MaterialTheme.typography.titleSmall
//            )
//        }
//        Card(
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Column(modifier = Modifier.padding(vertical = 8.dp)) {
//                DetailRow(label = "Timezone", value = ipLocation.timeZone.id)
//                DetailRow(label = "Current Time", value = ipLocation.timeZone.currentTime)
//                DetailRow(label = "GMT Offset", value = "${ipLocation.timeZone.gmtOffset / 3600}:00")
//                DetailRow(label = "Code", value = ipLocation.timeZone.code)
//                DetailRow(
//                    label = "Daylight Saving",
//                    value = if (ipLocation.timeZone.isDaylightSaving) "Yes" else "No"
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun CurrencyDetail(ipLocation: IpLocation) {
//    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 8.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Icon(
//                imageVector = Icons.Default.AttachMoney,
//                contentDescription = "Currency Details",
//                tint = MaterialTheme.colorScheme.primary
//            )
//            Text(
//                text = "Currency Details",
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.padding(start = 8.dp),
//                style = MaterialTheme.typography.titleSmall
//            )
//        }
//        Card(
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Column(modifier = Modifier.padding(vertical = 8.dp)) {
//                DetailRow(label = "Currency", value = "${ipLocation.currency.name} (${ipLocation.currency.code})")
//                DetailRow(label = "Symbol", value = "${ipLocation.currency.symbol} / ${ipLocation.currency.symbolNative}")
//                DetailRow(label = "Plural", value = ipLocation.currency.plural)
//            }
//        }
//    }
//}

