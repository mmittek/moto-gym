init;
data = loadCSVAndPreprocess('../Data/20161002_115147_542_90.csv');

idx = [ ACCX, ACCY, ACCZ, GYROX, GYROY, GYROZ, MAGX, MAGY, MAGZ ]
values = data(idx, idx);
cm = cov(values)
figure(1)
imagesc(cm)


% orientation ANGLE
state = zeros(6,1);

box = makeBox(0.5,1,4);

alpha = 0.9;

% user input acceleration
% state : gravity linearacc 
for i=2:size(data,1)
    dt = (data(i,1)-data(i-1,1))/1000;
    
    u = data(i,[ACCX, ACCY, ACCZ])';
    A = [ alpha 0 0 0 0 0
          0 alpha 0 0 0 0
           0 0 alpha 0 0 0
           1 0 0 0 0 0
           0 1 0 0 0 0
           0 0 1 0 0 0];
       
   B = [ 1-(alpha-dt) 0 0 
         0 1-(alpha-dt) 0 
         0 0 1-(alpha-dt) 
         -1 0 0 
         0 -1 0 
         0 0 -1 ];
     

    state = A*state + B*u;
    

    if(mod(i,10)==0)
        figure(1);
        clf;
        plotVec3D( state(1), state(2), state(3) );
        axis equal;
        axis([-10,10,-10,10,-10,10]);
        xlabel('x');
        ylabel('y');
        zlabel('z');
        grid on;
        drawnow;
    end

    
end
