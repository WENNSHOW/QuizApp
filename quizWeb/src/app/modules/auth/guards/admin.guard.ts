import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { UserStorageService } from '../services/user-storage.service';

@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {
  constructor(private router: Router) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): boolean {
    // Проверяем, что пользователь имеет роль ADMIN
    if (UserStorageService.isAdminLoggedIn()) {
      return true;
    }
    // Если пользователь не админ – перенаправляем на страницу "Доступ запрещён" или логина
    this.router.navigate(['/403']); // либо '/login', если у тебя нет страницы /403
    return false;
  }
}
