package com.example.suitmedia_intern

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.suitmedia_intern.adapters.UserAdapter
import com.example.suitmedia_intern.api.ApiService
import com.example.suitmedia_intern.databinding.ActivityMain3Binding
import com.example.suitmedia_intern.models.User
import com.example.suitmedia_intern.models.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity3 : AppCompatActivity() {

    private lateinit var binding: ActivityMain3Binding
    private lateinit var adapter: UserAdapter
    private val users = mutableListOf<User>()
    private var currentPage = 1
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = UserAdapter(users) { user ->
            val intent = Intent().apply {
                putExtra("SELECTED_USER_NAME", "${user.firstName} ${user.lastName}")
            }
            setResult(RESULT_OK, intent)
            finish()
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener {
            refreshData()
        }

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1) && !isLoading) {
                    loadMoreData()
                }
            }
        })

        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        loadMoreData()
    }

    private fun refreshData() {
        currentPage = 1
        users.clear()
        adapter.notifyDataSetChanged()
        loadMoreData()
    }

    private fun loadMoreData() {
        isLoading = true

        val retrofit = Retrofit.Builder()
            .baseUrl("https://reqres.in/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        apiService.getUsers(currentPage, 10).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    response.body()?.data?.let { newUsers ->
                        users.addAll(newUsers)
                        adapter.notifyDataSetChanged()
                        currentPage++
                    }
                }
                binding.swipeRefresh.isRefreshing = false
                isLoading = false
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                binding.swipeRefresh.isRefreshing = false
                isLoading = false
            }
        })
    }
}
