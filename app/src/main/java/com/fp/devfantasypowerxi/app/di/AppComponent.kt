package com.fp.devfantasypowerxi.app.di

import com.fp.devfantasypowerxi.app.di.module.AppModule
import com.fp.devfantasypowerxi.common.api.RestHelper
import com.fp.cricbull.common.di.module.RepositoryModule
import com.fp.devfantasypowerxi.app.view.VerifyOtpBtmSheet
import com.fp.devfantasypowerxi.app.view.activity.*
import com.fp.devfantasypowerxi.app.view.fragment.*
import com.fp.devfantasypowerxi.app.view.viewmodel.*
import com.fp.devfantasypowerxi.common.di.module.NetModule
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [AppModule::class, NetModule::class, RepositoryModule::class])
interface AppComponent {
    fun inject(restHelper: RestHelper?)
    fun inject(loginActivity: LoginActivity?)
    fun inject(registerActivity: RegisterActivity)
    fun inject(otpVerifyActivity: OtpVerifyActivity)
    fun inject(forgotPasswordActivity: ForgotPasswordActivity)
    fun inject(moreFragment: MoreFragment)
    fun inject(homeActivity: HomeActivity)
    fun inject(changePasswordActivity: ChangePasswordActivity)
    fun inject(personalDetailsActivity: PersonalDetailsActivity)
    fun inject(balanceFragment: BalanceFragment)
    fun inject(playingHistoryFragment: PlayingHistoryFragment)
    fun inject(homeFragment: HomeFragment)
    fun inject(mobileVerificationFragment: MobileVerificationFragment)
    fun inject(verifyOtpBtmSheet: VerifyOtpBtmSheet)
    fun inject(upComingMatchListViewModel: UpComingMatchListViewModel)
    fun inject(upComingContestActivity: UpComingContestActivity)
    fun inject(upComingContestFragment: UpComingContestFragment)
    fun inject(leaderBoardFragment: LeaderBoardFragment)
    fun inject(winningBreakUpFragment: WinningBreakUpFragment)
    fun inject(allContestActivity: AllContestActivity)
    fun inject(contestViewModel: ContestViewModel)
    fun inject(createTeamActivity: CreateTeamActivity)
    fun inject(verifyAccountActivity: VerifyAccountActivity)
    fun inject(createTeamViewModel: GetPlayerDataViewModel)
    fun inject(teamViewModel: TeamViewModel)
    fun inject(createTeamViewModel: CreateTeamViewModel)
    fun inject(chooseCandVCActivity: ChooseCandVCActivity)


}