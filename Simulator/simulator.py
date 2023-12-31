import pygame
import time
from abc import ABC, abstractmethod
from typing import List
from Map.obstacle import Obstacle
from Map.grid import Grid
from Settings.config import *
from Settings.colors import *
from Robot.robot import Robot
from GUI.button import Button


# Load button images
start_img = pygame.image.load("Assets/start_btn.png").convert_alpha()
exit_img = pygame.image.load("Assets/exit_btn.png").convert_alpha()
#reset_img = pygame.image.load("Assets/reset_btn.png").convert_alpha()


class AlgoApp(ABC):
    def __init__(self, obstacles: List[Obstacle]):
        self.grid = Grid(obstacles)
        self.robot = Robot(self.grid)
        
        self.start_button = Button(1000, 425, start_img, 1.05)
        self.exit_button = Button(1000, 910, exit_img, 1.2)
       # self.reset_button = Button(1000, 557, reset_img, 0.9)

    @abstractmethod
    def init(self):
        pass

    @abstractmethod
    def execute(self):
        pass


class AlgoSimulator(AlgoApp):
    """
    Run the algorithm using a GUI simulator.
    """
    def __init__(self, obstacles: List[Obstacle]):
        super().__init__(obstacles)

        self.running = False
        self.size = self.width, self.height = WINDOW_SIZE
        self.screen = self.clock = None
        self.time_cal = False

    def init(self):
        """
        Set initial values for the app.
        """
        pygame.init()
        self.running = True

        self.screen = pygame.display.set_mode(self.size, pygame.FULLSCREEN)  # pygame.HWSURFACE | pygame.DOUBLEBUF pygame.RESIZABLE
        self.clock = pygame.time.Clock()

        # Inform user that it is finding path...
        pygame.display.set_caption("Calculating path...")
        font = pygame.font.SysFont("Helvetica", 35)
        text = font.render("Calculating path...", True, WHITE)
        text_rect = text.get_rect()
        text_rect.center = WINDOW_SIZE[0] / 2, WINDOW_SIZE[1] / 2
        self.screen.blit(text, text_rect)
        pygame.display.flip()

    def settle_events(self):
        """
        Process Pygame events.
        """
        for event in pygame.event.get():
            # On quit, stop the game loop. This will stop the app.
            if event.type == pygame.QUIT:
                self.running = False

    def do_updates(self):
        self.robot.update()

    def render(self):
        """
        Render the screen.
        """
        rect_outer= pygame.Rect(0, 0, 1300, 1200)
        self.screen.fill(DARK_BLACK, rect=rect_outer)

        rect_grid = pygame.Rect(0, 0, 1000, 1000)
        self.screen.fill(MINT, rect=rect_grid)

        # Title 1
        font1 = pygame.font.SysFont("Helvetica", 26)
        text1 = font1.render("Algorithm Simulator", True, WHITE)
        text_rect1 = text1.get_rect()
        text_rect1 = 1000, 10
        self.screen.blit(text1, text_rect1)

        # Label 1
        font2 = pygame.font.SysFont("Helvetica", 24)
        text2 = font2.render("Image", True, DARK_YELLOW)
        text_rect2 = text2.get_rect()
        text_rect2 = 1040, 60
        self.screen.blit(text2, text_rect2)
        rect_label = pygame.Rect(1010, 64, 20, 20)
        self.screen.fill(DARK_YELLOW, rect=rect_label)

        # Label 2
        font4 = pygame.font.SysFont("Helvetica", 24)
        text4 = font4.render("Virtual obstacle border", True, RED)
        text_rect4 = text4.get_rect()
        text_rect4 = 1040, 110
        self.screen.blit(text4, text_rect4)
        rect_label2 = pygame.Rect(1010, 114, 20, 20)
        self.screen.fill(RED, rect=rect_label2)

        # Label 3
        font3 = pygame.font.SysFont("Helvetica", 24)
        text3 = font3.render("Forbidden", True, DARK_GREY)
        text_rect3 = text3.get_rect()
        text_rect3 = 1040, 160
        self.screen.blit(text3, text_rect3)
        rect_label3 = pygame.Rect(1010, 164, 20, 20)
        self.screen.fill(DARK_GREY, rect=rect_label3)

        # Label 4
        font5 = pygame.font.SysFont("Helvetica", 24)
        text5 = font5.render("Allowed", True, WHITE)
        text_rect5 = text5.get_rect()
        text_rect5 = 1040, 210
        self.screen.blit(text5, text_rect5)
        rect_label4 = pygame.Rect(1010, 214, 20, 20)
        self.screen.fill(WHITE, rect=rect_label4)

        self.grid.draw(self.screen)
        self.robot.draw(self.screen)

        # Draw start and exit buttons
        if self.start_button.draw():
            # Calculate the path.
            start = time.time()
            self.robot.brain.plan_path()
            time_delta = str(time.time() - start)
            print(time_delta)

        if self.exit_button.draw():
            self.running = False

       # if self.reset_button.draw():
           # pass

        # Really render now.
        pygame.display.flip()

    def execute(self):
        """
        Initialise the app and start the game loop.
        """
        while self.running:
            # Check for Pygame events.
            self.settle_events()
            # Do required updates.
            self.do_updates()
            # Render the new frame.
            self.render()


class AlgoMinimal(AlgoApp):
    """
    Minimal app to just calculate a path and then send the commands over.
    """
    def __init__(self, obstacles):
        super().__init__(obstacles)

    def init(self):
        pass

    def execute(self):
        print("Calculating path...")
        index_list = self.robot.brain.plan_path()
        return index_list