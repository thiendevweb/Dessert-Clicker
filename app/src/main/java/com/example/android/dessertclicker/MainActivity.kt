/*
 * Copyright 2020, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.dessertclicker

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import com.example.android.dessertclicker.databinding.ActivityMainBinding

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private var revenue = 0
    private var dessertsSold = 0

    // Contains all the views
    private lateinit var binding: ActivityMainBinding

    /** Dessert Data **/

    /**
     * Simple data class that represents a dessert. Includes the resource id integer associated with
     * the image, the price it's sold for, and the startProductionAmount, which determines when
     * the dessert starts to be produced.
     */
    data class Dessert(val imageId: Int, val price: Int, val startProductionAmount: Int)
    companion object {
        const val TAG = "MainActivity"
        const val KEY_REVENUE = "revenue_key"
        const val KEY_DESSERT_SOLD = "dessert_sold_key"
    }

    // Create a list of all desserts, in order of when they start being produced
    private val allDesserts = listOf(
        Dessert(R.drawable.cupcake, 5, 0),
        Dessert(R.drawable.donut, 10, 5),
        Dessert(R.drawable.eclair, 15, 20),
        Dessert(R.drawable.froyo, 30, 50),
        Dessert(R.drawable.gingerbread, 50, 100),
        Dessert(R.drawable.honeycomb, 100, 200),
        Dessert(R.drawable.icecreamsandwich, 500, 500),
        Dessert(R.drawable.jellybean, 1000, 1000),
        Dessert(R.drawable.kitkat, 2000, 2000),
        Dessert(R.drawable.lollipop, 3000, 4000),
        Dessert(R.drawable.marshmallow, 4000, 8000),
        Dessert(R.drawable.nougat, 5000, 16000),
        Dessert(R.drawable.oreo, 6000, 20000)
    )
    private var currentDessert = allDesserts[0]

    //lylifecycle callback methods
    // ở trạng thái started và resumed , activity có thể nhìn thấy
    // ở trạng thái resumed , acivity đang focus, hay là đang hiển thị và tương tác trên activity
    //Bất kỳ mã nào chạy trong onPause () đều chặn những thứ khác hiển thị, vì vậy hãy giữ cho mã
    // trong onPause () nhẹ. Ví dụ: nếu có cuộc gọi đến, mã trong onPause ()    có thể trì hoãn thông báo cuộc gọi đến.
    override fun onPause() {
        super.onPause()
        Log.v(TAG, "onPause is callback...")
    }

    //    Phương thức onRestart () là nơi bạn viết code mà bạn chỉ muốn gọi nếu activity của bạn
//    không được bắt đầu lần đầu tiên.
    override fun onRestart() {
        super.onRestart()
        Log.v(TAG, "onRestart is callback...")
    }

    override fun onStop() {
        super.onStop()
        Log.v(TAG, "onStop is callback...")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "onDestroy is callback...")
    }

    override fun onResume() {
        super.onResume()
        Log.v(TAG, "onResumed is callback...")
    }

    //Phương thức vòng đời onStart () được gọi ngay sau onCreate ().
    // Sau khi onStart () chạy, hoạt động của bạn sẽ hiển thị trên màn hình.(do nó chuyển tới Started state)
    // Không giống như onCreate (), chỉ được gọi một lần để khởi tạo hoạt động của bạn,
    // onStart () có thể được gọi nhiều lần trong vòng đời hoạt động của bạn.
    //Lưu ý rằng onStart () được ghép nối với một phương thức vòng đời onStop () tương ứng.
    // Nếu người dùng khởi động ứng dụng của bạn và sau đó quay lại màn hình chính của thiết bị,
    // hoạt động sẽ bị dừng và không còn hiển thị trên màn hình.
    override fun onStart() {
        super.onStart()
        Log.v(TAG, "onStart is callback...")
    }

    //Phương thức onCreate () là nơi bạn nên thực hiện bất kỳ khởi tạo một lần nào cho hoạt động của mình.
//    Phương thức vòng đời onCreate () được gọi một lần, ngay sau khi hoạt động được khởi tạo
//    (khi đối tượng Hoạt động mới được tạo trong bộ nhớ). Sau khi onCreate () thực thi, hoạt động được coi
//    là đã tạo.
    //sau khi onCreate được gọi, activity lifecycle sẽ chuyển tới trạng thái Created
    override fun onCreate(savedInstanceState: Bundle?) {

        //gọi onCreate của parent để init Activity
        super.onCreate(savedInstanceState)
        Log.v(TAG, "onCreate is callback....")
        // Use Data Binding to get reference to the views
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        //Restore activity state
        if (savedInstanceState != null) {
            revenue = savedInstanceState.getInt(KEY_REVENUE, 0)
            dessertsSold = savedInstanceState.getInt(KEY_DESSERT_SOLD, 0)
            showCurrentDessert()
        }
        binding.dessertButton.setOnClickListener {
            onDessertClicked()
        }

        // Set the TextViews to the right values
        binding.revenue = revenue
        binding.amountSold = dessertsSold

        // Make sure the correct dessert is showing
        binding.dessertButton.setImageResource(currentDessert.imageId)
    }

    //    Phương thức onSaveInstanceState () là một lệnh gọi lại mà bạn sử dụng để lưu bất kỳ dữ liệu nào mà bạn có
//    thể cần nếu Hoạt động bị phá hủy. Trong sơ đồ gọi lại vòng đời, onSaveInstanceState () được gọi sau khi
//    hoạt động đã bị dừng. Nó được gọi mỗi khi ứng dụng của bạn chuyển sang chế độ nền.
    //Gói là một tập hợp các cặp khóa-giá trị, trong đó các khóa luôn là chuỗi. Bạn có thể đặt dữ liệu đơn giản, chẳng hạn như giá trị Int và Boolean, vào gói.
    // Vì hệ thống giữ gói này trong bộ nhớ, nên cách tốt nhất là giữ cho dữ liệu trong gói nhỏ. Kích thước của gói này cũng bị hạn chế, mặc dù kích thước khác nhau giữa
    // các thiết bị. Nếu bạn lưu trữ quá nhiều dữ liệu, bạn có nguy cơ gặp sự cố ứng dụng của mình với lỗi TransactionTooLargeException.
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_REVENUE, revenue)
        outState.putInt(KEY_DESSERT_SOLD, dessertsSold)
        Log.d(TAG, "onSaveInstanceState is callback...")
    }

    // có thể khôi phục bundle data trong 2 method callback : onCreate or onRestoreInstanceState
    //savedInstanceState và savedInstanceState cũng chính là outState trong onSaveInstanceState
    //Nếu hoạt động đang được tạo lại, lệnh gọi lại onRestoreInstanceState () được gọi sau onStart ()
    //Hầu hết thời gian, bạn khôi phục trạng thái hoạt động trong onCreate ().
    // Nhưng vì onRestoreInstanceState () được gọi sau onStart (), nếu bạn cần khôi phục một số trạng thái
    // sau khi onCreate () được gọi, bạn có thể sử dụng onRestoreInstanceState ().
//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//        // mình phải khôi phục lại cả dữ liệu của variable , vì nếu chỉ khôi phục mỗi text, :vv do thằng dau buoi nào viet code như cc
//        revenue = savedInstanceState.getInt(KEY_REVENUE, 0)
//        dessertsSold = savedInstanceState.getInt(KEY_DESSERT_SOLD, 0)
//        binding.revenue = revenue
//        binding.amountSold = dessertsSold
//        Log.d(TAG, "onRestoreInstanceState is callback...")
//    }

    /**
     * Updates the score when the dessert is clicked. Possibly shows a new dessert.
     */
    private fun onDessertClicked() {

        // Update the score
        revenue += currentDessert.price
        dessertsSold++

        binding.revenue = revenue
        binding.amountSold = dessertsSold

        // Show the next dessert
        showCurrentDessert()
    }

    /**
     * Determine which dessert to show.
     */
    private fun showCurrentDessert() {
        var newDessert = allDesserts[0]
        for (dessert in allDesserts) {
            if (dessertsSold >= dessert.startProductionAmount) {
                newDessert = dessert
            }
            // The list of desserts is sorted by startProductionAmount. As you sell more desserts,
            // you'll start producing more expensive desserts as determined by startProductionAmount
            // We know to break as soon as we see a dessert who's "startProductionAmount" is greater
            // than the amount sold.
            else break
        }

        // If the new dessert is actually different than the current dessert, update the image
        if (newDessert != currentDessert) {
            currentDessert = newDessert
            binding.dessertButton.setImageResource(newDessert.imageId)
        }
    }

    /**
     * Menu methods
     */
    private fun onShare() {
        val shareIntent = ShareCompat.IntentBuilder.from(this)
            .setText(getString(R.string.share_text, dessertsSold, revenue))
            .setType("text/plain")
            .intent
        try {
            startActivity(shareIntent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                this, getString(R.string.sharing_not_available),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.shareMenuButton -> onShare()
        }
        return super.onOptionsItemSelected(item)
    }
}
//Khi một hoạt động bắt đầu lại từ đầu, bạn sẽ thấy cả ba lệnh gọi lại vòng đời này được gọi theo thứ tự:
//
//onCreate () để tạo ứng dụng.
//onStart () để khởi động nó và hiển thị nó trên màn hình.
//onResume () để cung cấp cho hoạt động trọng tâm và làm cho nó sẵn sàng cho người dùng tương tác với nó.
//Mặc dù có tên, phương thức onResume () được gọi khi khởi động, ngay cả khi không có gì để tiếp tục.
// Activity cũng có thể bị tắt hoàn toàn nếu như chúng ta gọi finish() method
//onCreate () để khởi tạo ứng dụng lần đầu tiên và
// onDestroy () để dọn dẹp tài nguyên được sử dụng bởi ứng dụng của bạn.
//Hoạt động của bạn không đóng hoàn toàn mỗi khi người dùng điều hướng khỏi hoạt động đó:
//Vậy còn onRestart () thì sao? Phương thức onRestart () giống như onCreate ().
// OnCreate () hoặc onRestart () được gọi trước khi hoạt động hiển thị.
// Phương thức onCreate () chỉ được gọi lần đầu tiên và onRestart () được gọi sau đó.
// Phương thức onRestart () là nơi đặt mã mà bạn chỉ muốn gọi nếu hoạt động của bạn không được bắt đầu
// lần đầu tiên.
//Bạn đã biết rằng khi một ứng dụng được khởi động và onStart () được gọi, ứng dụng đó sẽ hiển thị trên màn hình.
// Khi ứng dụng được tiếp tục và onResume () được gọi, ứng dụng sẽ thu hút được sự tập trung của người dùng,
// tức là người dùng có thể tương tác với ứng dụng.
//changes so radically that the easiest way for the system to resolve the change is to completely shut down and rebuild the activity. For example, if the user changes the device language, the whole layout might need to change to accommodate different text directions and string lengths. If the user plugs the device into a dock or adds a physical keyboard, the app layout may need to take advantage of a different display size or layout. And if the device orientation changes—if the device is rotated from portrait to landscape or back the other way—the layout may need to change to fit the new orientation. Let's look at how the app behaves in this scenario.
//717 / 5.000
//Kết quả dịch
//Thay đổi cấu hình xảy ra khi trạng thái của thiết bị thay đổi hoàn toàn đến mức cách dễ nhất để hệ thống giải
// quyết thay đổi là tắt hoàn toàn và xây dựng lại hoạt động. Ví dụ: nếu người dùng thay đổi ngôn ngữ thiết bị,
// toàn bộ bố cục có thể cần phải thay đổi để phù hợp với các hướng văn bản và độ dài chuỗi khác nhau.