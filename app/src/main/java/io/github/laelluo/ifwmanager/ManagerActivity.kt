package io.github.laelluo.ifwmanager

import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import io.github.laelluo.ifwmanager.bean.AppBean
import kotlinx.android.synthetic.main.activity_manager.*

class ManagerActivity : AppCompatActivity() {
    private var app: AppBean? = null
    private var services: List<String> = listOf()
    private var receivers: List<String> = listOf()
    private var activities: List<String> = listOf()
    private var providers: List<String> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager)
//        初始化数据
        app = Data.apps[intent.extras.getInt("position")]
        packageManager.getPackageInfo(app!!.applicationInfo.packageName, PackageManager.GET_SERVICES).services?.map { it.name }?.let { services = it }
        packageManager.getPackageInfo(app!!.applicationInfo.packageName, PackageManager.GET_RECEIVERS).receivers?.map { it.name }?.let { receivers = it }
        packageManager.getPackageInfo(app!!.applicationInfo.packageName, PackageManager.GET_ACTIVITIES).activities?.map { it.name }?.let { activities = it }
        packageManager.getPackageInfo(app!!.applicationInfo.packageName, PackageManager.GET_PROVIDERS).providers?.map { it.name }?.let { providers = it }

//        初始化组件
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
        name_text_manager.text = app?.label
        packagename_text_manager.text = app?.applicationInfo?.packageName
        icon_image_manager.setImageDrawable(app?.icon)
//        初始化tab viewpager 碎片
        tabs.setupWithViewPager(viewpager)
        setUpViewPager(viewpager)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_manager, menu)
        menu?.findItem(R.id.search_item_menu_manager)?.apply {
            //            搜索触发事件

//            TODO(ManagerActivity搜索)
            setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                    return true
                }

                override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                    return true
                }

            })
//            输入触发事件
            actionView?.let { it as SearchView }?.apply {
                maxWidth = 1000
                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?) = true

                    override fun onQueryTextChange(newText: String?): Boolean {
                        return true
                    }
                })
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
//        TODO(MENU功能)
        if (item?.itemId == android.R.id.home) {
            finish()
        }
        return true
    }

    private fun setUpViewPager(viewPager: ViewPager) {
        val adapter = ComponentsAdapter(supportFragmentManager)
        adapter.addFragment(ComponentFragment.newInstance(services.toTypedArray(), "Services"), "Services")
        adapter.addFragment(ComponentFragment.newInstance(receivers.toTypedArray(), "Receivers"), "Receivers")
        adapter.addFragment(ComponentFragment.newInstance(activities.toTypedArray(), "Activities"), "Activities")
        adapter.addFragment(ComponentFragment.newInstance(providers.toTypedArray(), "Providers"), "Providers")
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
