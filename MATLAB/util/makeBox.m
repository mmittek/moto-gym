function [pc] = box(w,h,ndims)
    box2d = [ -w/2,-h/2; -w/2,h/2; w/2,h/2; w/2,-h/2; -w/2,-h/2 ];
    box3d = [box2d zeros(size(box2d,1),1)];
    box4d = [ box3d zeros(size(box3d,1),1) ];
    if(ndims == 2)
        pc = box2d;
        return;
    end
    
    if(ndims == 3)
        pc = box3d;
        return;
    end

    if(ndims==4)
        pc = box4d;
        return;
    end
    
end