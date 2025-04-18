package com.example.revisly

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.revisly.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
     lateinit var navController : NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.HomeContainer) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomBar?.setOnItemSelectedListener {



            when(it.itemId){
                (R.id.homemenu) -> {
                    navController.navigate(R.id.homeFragment)

                }
                (R.id.postmenu) -> {
                    navController.navigate(R.id.postViewFragment)


                }
            }



            return@setOnItemSelectedListener true
        }


//        val navControl = findNavController(R.id.HomeContainer)

        binding.HomePage?.setOnClickListener {
            navController.navigate(R.id.homeFragment)

        }

        binding.PostPage?.setOnClickListener {
            navController.navigate(R.id.postViewFragment)

        }

    }
}