package uk.ac.tees.mad.iplocator.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavHostController) {
    var query by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        DockedSearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding).padding(16.dp),
            inputField = {
                SearchBarDefaults.InputField(modifier = Modifier.fillMaxWidth(),
                    query = query,
                    onQueryChange = { newQuery ->
                        query = newQuery
                        //viewModel.searchIP(newQuery)
                    },
                    onSearch = { newQuery ->
                        expanded = false
//                                    query = newQuery
//                                    viewModel.searchIP(newQuery)
                    },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = { Text("Search") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search, contentDescription = null
                        )
                    },
                    trailingIcon = {
                        if(expanded){
                            IconButton(onClick = {
                                if (query.isNotBlank()) {
                                    query = ""
                                    //viewModel.searchIP(query)
                                }
                                expanded = false


                            }) {
                                Icon(Icons.Default.Close, contentDescription = null)
                            }}

                    })
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            if (query.isNotBlank()) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {

                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Start Typing to Search")
                }
            }
        }
    }
}