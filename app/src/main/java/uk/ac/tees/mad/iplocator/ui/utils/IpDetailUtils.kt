package uk.ac.tees.mad.iplocator.ui.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.svg.SvgDecoder
import uk.ac.tees.mad.iplocator.R
import uk.ac.tees.mad.iplocator.model.dataclass.IpLocation

@Composable
fun HeaderCard(header: String, ipLocation: IpLocation) {
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
                    text = header,
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
fun CountryFlag(label: String, url: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "$label:", fontWeight = FontWeight.Bold)
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(url)
                .decoderFactory(SvgDecoder.Factory()).crossfade(true).size(300).build(),
            contentDescription = "Country Flag",
            loading = {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .shimmerEffect()
                        .clip(RoundedCornerShape(16.dp))
                )
            },
            error = {
                Image(
                    painter = painterResource(id = R.drawable.baseline_broken_image_24),
                    contentDescription = "Error loading Image",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.FillBounds
                )
            },
            modifier = Modifier.clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.FillBounds
        )
    }
}

@Composable
fun LocationDetail(ipLocation: IpLocation) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
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
                    label = "Region", value = "${ipLocation.regionName} (${ipLocation.regionCode})"
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
            }
        }
    }
}

@Composable
fun Coordinates(ipLocation: IpLocation) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Coordinates",
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Coordinate Details",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.titleSmall
            )
        }
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                DetailRow(
                    label = "Latitude", value = "${ipLocation.latitude}"
                )
                DetailRow(
                    label = "Longitude", value = "${ipLocation.longitude}"
                )
            }
        }
    }
}

@Composable
fun ISPDetail(ipLocation: IpLocation) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
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
                DetailRow(label = "Connection Type", value = ipLocation.connectionType.toString())
            }
        }
    }
}

@Composable
fun AdditionalInfo(ipLocation: IpLocation) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Additional Info",
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Additional Info",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.titleSmall
            )
        }
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                DetailRow(label = "Capital", value = ipLocation.location?.capital.toString())
                DetailRow(label = "Calling Code", value = "+${ipLocation.location?.callingCode}")
                DetailRow(
                    label = "Flag Emoji", value = "${ipLocation.location?.countryFlagEmoji}"
                )
                CountryFlag(label = "Country Flag", url = "${ipLocation.location?.countryFlag}")
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