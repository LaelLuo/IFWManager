package io.github.laelluo.ifwmanager

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import io.github.laelluo.ifwmanager.bean.AppBean
import kotlinx.android.synthetic.main.activity_manager.*

class ManagerActivity : AppCompatActivity() {
    private lateinit var app: AppBean
    private lateinit var serviceFragment: ComponentFragment
    private lateinit var receiverFragment: ComponentFragment
    private lateinit var activityFragment: ComponentFragment
    private lateinit var providerFragment: ComponentFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager)
//        初始化数据
        app = Data.apps[intent.extras.getInt("position")]
        serviceFragment = ComponentFragment.newInstance(app.services.toTypedArray(), "Services")
        receiverFragment = ComponentFragment.newInstance(app.receivers.toTypedArray(), "Receivers")
        activityFragment = ComponentFragment.newInstance(app.activities.toTypedArray(), "Activities")
        providerFragment = ComponentFragment.newInstance(app.providers.toTypedArray(), "Providers")

//        初始化组件
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
        @SuppressLint("SetTextI18n")
        name_text_manager.text = app.label + " " + app.version
        packagename_text_manager.text = app.packageName
        icon_image_manager.setImageDrawable(app.icon)
//        初始化tab viewpager 碎片
        tabs.setupWithViewPager(viewpager)
        setUpViewPager(viewpager)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_manager, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> finish()
            R.id.save_item_menu_main -> {
                log(serviceFragment.getBanList()[0])
            }
        }
        return true
    }

    private fun setUpViewPager(viewPager: ViewPager) {
        val adapter = ComponentsAdapter(supportFragmentManager)
        adapter.addFragment(serviceFragment, "Services")
        adapter.addFragment(receiverFragment, "Receivers")
        adapter.addFragment(activityFragment, "Activities")
        adapter.addFragment(providerFragment, "Providers")
        viewPager.adapter = adapter
    }

    private class ComponentsAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
        private val tabContents = ArrayList<Fragment>()
        private val tabTitles = ArrayList<String>()
        fun addFragment(fragment: Fragment, title: String) {
            tabContents.add(fragment)
            tabTitles.add(title)
        }

        override fun getItem(position: Int) = tabContents[position]
        override fun getCount() = tabContents.size
        override fun getPageTitle(position: Int) = tabTitles[position]
    }

}
