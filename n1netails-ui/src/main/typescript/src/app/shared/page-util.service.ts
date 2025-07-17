import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PageRequest } from '../model/interface/page.interface';

@Injectable({
  providedIn: 'root'
})
export class PageUtilService {

  constructor() { }

  getPageRequestParams(pageRequest: PageRequest): HttpParams {
    let params = new HttpParams()
      .set('pageNumber', pageRequest.pageNumber)
      .set('pageSize', pageRequest.pageSize)
      .set('sortDirection', pageRequest.sortDirection)
      .set('sortBy', pageRequest.sortBy);

    if (pageRequest.searchTerm) {
      params = params.set('searchTerm', pageRequest.searchTerm);
    }
    return params;
  }

  setDefaultPageRequest(): PageRequest {
    const pageRequest: PageRequest = {
      pageNumber: 0,
      pageSize: 50,
      sortDirection: "ASC",
      sortBy: "id"
    };
    return pageRequest;
  }

  setDefaultPageRequestWithSearch(term: string): PageRequest {
    const pageRequest: PageRequest = {
      pageNumber: 0,
      pageSize: 50,
      sortDirection: "ASC",
      sortBy: "id",
      searchTerm: term
    };
    return pageRequest;
  }
}
